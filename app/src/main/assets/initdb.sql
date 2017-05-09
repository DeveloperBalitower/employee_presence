CREATE TABLE IF NOT EXISTS workplaces
(
workplace_id TEXT NOT NULL,
workplace_name TEXT,
latitude TEXT,
longitude TEXT,
address TEXT,

PRIMARY KEY(workplace_id)
)$

CREATE TABLE employee_precences_history (
  id INTEGER PRIMARY KEY   AUTOINCREMENT,
  server_id INTEGER,
  user_nik TEXT,
  check_in_date TEXT,
  check_in_time TEXT,
  checkin_lat DOUBLE,
  checkin_long DOUBLE,
  check_out_date TEXT,
  check_out_time TEX,
  checkout_lat DOUBLE,
  checkout_long DOUBLE,
  site TEXT,
  updated_at TEXT,
  created_at TEXT,
  upload_status INTEGER DEFAULT 0
)$

CREATE TABLE IF NOT EXISTS `employee_presences_setting` (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    setting_name TEXT    UNIQUE,
    value_int    INTEGER DEFAULT (0),
    value_string TEXT,
    activated    INTEGER DEFAULT (0)
)$