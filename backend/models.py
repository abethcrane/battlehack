from server import db

class Vendor(db.Model):
  id = db.Column(db.Integer, primary_key=True)
  bluetooth = db.Column(db.String(80), unique=True)
  vendor = db.Column(db.String(80))

  @classmethod
  def filter_by_ids(cls, ids):
    rows = cls.query.filter(cls.bluetooth.in_(ids))
    return rows
