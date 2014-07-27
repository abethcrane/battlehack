from server import app, db
import models

from flask import redirect, url_for, render_template, request
from flask.ext import admin, login
from flask.ext.admin import expose, helpers
from flask.ext.admin.contrib import sqla


class BaseOrganisationFilteredModelView(sqla.ModelView):
  def get_query(self):
    q = super(BaseOrganisationFilteredModelView, self).get_query()
    q = q.filter(self.model.organisation==login.current_user.organisation)
    return q

  def is_accessible(self):
    return login.current_user.is_authenticated()


class VendorAdmin(BaseOrganisationFilteredModelView):
  column_labels = dict(bluetooth='Bluetooth ID', vendor='Name')
  column_searchable_list = (models.Vendor.bluetooth, models.Vendor.vendor)
  form_excluded_columns = ('total_sold',)

  column_list = ('organisation', 'bluetooth', 'vendor', 'price', 'image', 'item_name')

  def __init__(self, session):
    super(VendorAdmin, self).__init__(models.Vendor, session, name='Individual vendors')


class OrdersView(BaseOrganisationFilteredModelView):
  can_create = False
  can_edit = False
  can_delete = False

  column_sortable_list = ('vendor', 'item', 'price', 'timestamp')

  def __init__(self, session):
    super(OrdersView, self).__init__(models.Order, session, name='Order history')


class BalancesView(admin.BaseView):
  @expose('/')
  @login.login_required
  def index(self):
    vendors = models.Vendor.query.filter(models.Vendor.organisation==login.current_user.organisation)
    return render_template('admin/balances.html',
        admin_base_template='admin/base.html',
        admin_view=self,
        h=helpers,
        vendors=vendors,
        org_name=login.current_user.organisation.name)


  @expose('/pay/<vid>')
  @login.login_required
  def pay(self, vid):
    vendor = models.Vendor.get_by_id(vid)
    vendor.pay_up()
    db.session.commit()
    return redirect('/admin/balancesview/')


class LogoutAdmin(admin.BaseView):
  @expose('/')
  def index(self):
    return redirect('/logout')

class AdminIndexView(admin.AdminIndexView):
  @expose('/')
  @login.login_required
  def index(self):
    return render_template('admin/index.html',
        admin_base_template='admin/base.html',
        admin_view=self,
        h=helpers,
        org_name=login.current_user.organisation.name)

@app.route('/admin/bulk', methods=['POST'])
@login.login_required
def bulk_update():
  name = request.form.get('name', None)
  keyword = request.form.get('keyword', None)
  price = request.form.get('price', None)

  try:
    price = int(price)
  except:
    price = None

  models.Vendor.update_bulk(login.current_user, name, keyword, price)
  return redirect('/admin')

admin = admin.Admin(app, index_view=AdminIndexView(name='Bulk update'))
admin.add_view(VendorAdmin(db.session))
admin.add_view(OrdersView(db.session))
admin.add_view(BalancesView(name='Balances'))
admin.add_view(LogoutAdmin(name='Logout'))
