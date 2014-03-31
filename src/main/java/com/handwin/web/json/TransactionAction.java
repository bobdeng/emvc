package com.handwin.web.json;

import com.handwin.db.HibernateTool;
/**
 * 用户数据库应用访问的Action的父类。
 * 在子类Action中的任何动作，都无需提交事务，由父类来完成。
 * 在提交事务失败后，会自动回滚，并调用子类onTransactionException方法，来获取返回数据
 * @author Administrator
 *
 */
public abstract class TransactionAction extends JsonAction {

	@Override
	public Object execute() {
		// TODO Auto-generated method stub
		try
		{
			Object rlt=this.exec();
			HibernateTool.commitTransaction();
			return rlt;
		}catch(Exception e)
		{
			HibernateTool.rollbackTransaction();
			e.printStackTrace();
			return this.onTransactionException(e);
		}
	}
	/**
	 * 实际的操作方法
	 * @return
	 */
	public abstract Object exec()throws Exception;
	/**
	 * 在数据库操作出错的时候回调，返回对象
	 * @param e
	 * @return
	 */
	public abstract Object onTransactionException(Exception e);

}
