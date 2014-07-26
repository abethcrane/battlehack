import bcrypt, datetime

from server import db

class Vendor(db.Model):
  id = db.Column(db.Integer, primary_key=True)
  bluetooth = db.Column(db.String(100), unique=True)
  vendor = db.Column(db.Unicode(100))
  keyword = db.Column(db.Unicode(100))
  price = db.Column(db.Integer())
  image = db.Column(db.Unicode(100))
  item_name = db.Column(db.Unicode(100))

  organisation = db.relationship('Organisation')
  organisation_id = db.Column(db.Integer(), db.ForeignKey('organisation.id'))

  def __unicode__(self):
    return self.vendor

  @classmethod
  def filter_by_ids(cls, ids):
    rows = cls.query.filter(cls.bluetooth.in_(ids))
    return rows

  @classmethod
  def get_by_id(cls, vid):
    return cls.query.filter(cls.id==vid).first()

  @classmethod
  def update_bulk(cls, user, bluetooth, name, keyword, price):
    values = dict(bluetooth=bluetooth, vendor=name, keyword=keyword, price=price)
    values = {k:v for k,v in values.iteritems() if v}
    cls.query.filter(cls.organisation==user.organisation).update(values)
    db.session.commit()


class User(db.Model):
  id = db.Column(db.Integer, primary_key=True)
  username = db.Column(db.Unicode(1000), nullable=False)
  password = db.Column(db.String(1000), nullable=False)
  organisation = db.relationship('Organisation')
  organisation_id = db.Column(db.Integer(), db.ForeignKey('organisation.id'))

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

class Organisation(db.Model):
  id = db.Column(db.Integer, primary_key=True)
  name = db.Column(db.Unicode(100), unique=True, nullable=False)

  def __unicode__(self):
    return self.name

class Order(db.Model):
  id = db.Column(db.Integer, primary_key=True)

  organisation = db.relationship('Organisation', backref='orders')
  organisation_id = db.Column(db.Integer(), db.ForeignKey('organisation.id'))

  vendor = db.relationship('Vendor')
  vendor_id = db.Column(db.Integer(), db.ForeignKey('vendor.id'))

  item = db.Column(db.Unicode(100))
  price = db.Column(db.Integer())
  timestamp = db.Column(db.DateTime(), default=datetime.datetime.utcnow)

  def __init__(self, org, vend, item, price):
    self.organisation = org
    self.vendor = vend
    self.item = item
    self.price = price
