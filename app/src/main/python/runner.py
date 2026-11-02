# -*- coding: utf-8 -*-
"""پل بین اپ اندروید و curlybot.py — بدون دست‌زدن به کد اصلی ربات."""
import asyncio
import collections
import logging
import os
import socket
import sys
import threading
import time

LOGS = collections.deque(maxlen=700)
_state = {"thread": None, "loop": None, "task": None, "started": 0.0}
_handler_added = False


def _log(msg):
    LOGS.append(time.strftime("%H:%M:%S") + "  " + str(msg))


class _LogHandler(logging.Handler):
    def emit(self, record):
        try:
            icon = {"ERROR": "❌", "CRITICAL": "❌", "WARNING": "⚠️"}.get(record.levelname, "•")
            _log(f"{icon} {record.getMessage()}")
        except Exception:
            pass


class _Tee:
    def write(self, s):
        s = str(s).strip()
        if s:
            _log(s)
        return len(s)

    def flush(self):
        pass


def _free_port():
    s = socket.socket()
    s.bind(("127.0.0.1", 0))
    port = s.getsockname()[1]
    s.close()
    return port


def _attach_logging():
    global _handler_added
    root = logging.getLogger()
    root.setLevel(logging.INFO)
    if not _handler_added:
        root.addHandler(_LogHandler())
        _handler_added = True


def _run():
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    _state["loop"] = loop
    try:
        sys.modules.pop("curlybot", None)   # هر بار تازه، تا حالت داخلی قبلی نماند
        import curlybot
        _attach_logging()
        _log("🚀 ربات در حال اتصال به روم...")
        task = loop.create_task(curlybot.main())
        _state["task"] = task
        loop.run_until_complete(task)
    except asyncio.CancelledError:
        pass
    except BaseException as e:  # noqa
        _log(f"❌ خطا: {e}")
    finally:
        try:
            pend = [t for t in asyncio.all_tasks(loop) if not t.done()]
            for t in pend:
                t.cancel()
            if pend:
                loop.run_until_complete(asyncio.gather(*pend, return_exceptions=True))
        except Exception:
            pass
        try:
            loop.close()
        except Exception:
            pass
        _state.update({"loop": None, "task": None, "started": 0.0})
        _log("⏹ ربات متوقف شد")


def start(room_id, token, owner, admins, files_dir):
    th = _state["thread"]
    if th is not None and th.is_alive():
        return
    os.chdir(files_dir)
    os.environ["ROOM_ID"] = str(room_id).strip()
    os.environ["API_TOKEN"] = str(token).strip()
    os.environ["OWNER_USERNAME"] = str(owner).strip().lstrip("@")
    os.environ["ADMIN_USERNAMES"] = str(admins).strip()
    os.environ["PORT"] = str(_free_port())
    sys.stdout = _Tee()
    sys.stderr = _Tee()
    _state["started"] = time.time()
    th = threading.Thread(target=_run, name="curlybot", daemon=True)
    _state["thread"] = th
    th.start()


def stop():
    loop, task, th = _state["loop"], _state["task"], _state["thread"]
    if loop is not None and task is not None:
        loop.call_soon_threadsafe(task.cancel)
    if th is not None:
        th.join(8)


def is_running():
    th = _state["thread"]
    return bool(th is not None and th.is_alive())


def uptime():
    return int(time.time() - _state["started"]) if _state["started"] and is_running() else 0


def get_logs():
    return "\n".join(LOGS)


def clear_logs():
    LOGS.clear()
