import bcrypt

from server import db

class Vendor(db.Model):
  id = db.Column(db.Integer, primary_key=True)
  bluetooth = db.Column(db.String(100), unique=True)
  vendor = db.Column(db.Unicode(100))
  keyword = db.Column(db.Unicode(100))
  price = db.Column(db.Integer())

  @classmethod
  def filter_by_ids(cls, ids):
    rows = cls.query.filter(cls.bluetooth.in_(ids))
    return rows

  @classmethod
  def get_by_id(cls, vid):
    return cls.query.filter(cls.id==vid).first()


class User(db.Model):
  id = db.Column(db.Integer, primary_key=True)
  username = db.Column(db.Unicode(1000), nullable=False)
  password = db.Column(db.String(1000), nullable=False)

  @classmethod
  def _encrypt_pass(cls, password):
    return bcrypt.hashpw(password, bcrypt.gensalt())

  @classmethod
  def get_by_username(cls, username):
    return cls.query.filter(User.username==username).first()

  @classmethod
  def authenticate_or_none(cls, username, password):
    u = cls.get_by_username(username)
    return u if u and u.check_password(password) else None

  def set_password(self, password):
    self.raw_password = password
    self.password = self._encrypt_pass(password)

  def check_password(self, password):
    password = password.encode('utf-8')
    hashed = self.password.encode('utf-8')
    return hashed is not None and bcrypt.hashpw(password, hashed) == hashed

  def get_id(self):
    return self.username

  def is_active(self):
    return True

  def is_authenticated(self):
    return True

  def is_anonymous(self):
    return False
