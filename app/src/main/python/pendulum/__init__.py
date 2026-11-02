"""Minimal stdlib-only stand-in for pendulum 2.x (Android build).

highrise-bot-sdk فقط `DateTime` و `parse` را از pendulum می‌خواهد.
pendulum اصلی روی اندروید خطای «Locale [en] does not exist» می‌دهد،
پس این نسخه‌ی سبک جایش را می‌گیرد.
"""
from datetime import date as _date, datetime as _dt, time as _time, timedelta, timezone

__version__ = "2.1.2"
UTC = timezone.utc


class DateTime(_dt):
    @property
    def int_timestamp(self):
        return int(self.timestamp())

    @property
    def float_timestamp(self):
        return self.timestamp()

    def to_iso8601_string(self):
        return self.isoformat()

    def to_datetime_string(self):
        return self.strftime("%Y-%m-%d %H:%M:%S")


class Date(_date):
    pass


class Time(_time):
    pass


Duration = timedelta
Period = timedelta


def _wrap(d):
    return DateTime(d.year, d.month, d.day, d.hour, d.minute, d.second, d.microsecond, tzinfo=d.tzinfo)


def parse(text, tz=None, **_kw):
    s = str(text).strip()
    if s[-1:] in ("Z", "z"):
        s = s[:-1] + "+00:00"
    try:
        d = _dt.fromisoformat(s)
    except ValueError:
        d = None
        for fmt in ("%Y-%m-%dT%H:%M:%S.%f%z", "%Y-%m-%dT%H:%M:%S%z",
                    "%Y-%m-%d %H:%M:%S", "%Y-%m-%d"):
            try:
                d = _dt.strptime(s, fmt)
                break
            except ValueError:
                continue
        if d is None:
            raise ValueError("Unable to parse date: %r" % (text,))
    if d.tzinfo is None:
        d = d.replace(tzinfo=timezone.utc)
    return _wrap(d)


def now(tz=None):
    return _wrap(_dt.now(timezone.utc))


def today(tz=None):
    return now(tz)


def from_timestamp(ts, tz=None):
    return _wrap(_dt.fromtimestamp(ts, timezone.utc))


def datetime(year, month, day, hour=0, minute=0, second=0, microsecond=0, tz=None):
    return DateTime(year, month, day, hour, minute, second, microsecond, tzinfo=timezone.utc)


def duration(**kw):
    return timedelta(**kw)
