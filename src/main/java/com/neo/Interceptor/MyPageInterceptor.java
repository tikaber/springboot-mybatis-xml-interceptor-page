package com.neo.Interceptor;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

/**
 * @Intercepts 声明是一个拦截器
 * @Signature 拦截器的签名
 * type 拦截的类型 四大对象之一( Executor,ResultSetHandler,ParameterHandler,StatementHandler)
 * method 拦截的方法
 * args 参数
 */
@Component
@Intercepts(@Signature(type=StatementHandler.class, method="prepare", args= {Connection.class, Integer.class}))
public class MyPageInterceptor implements Interceptor {
	
	//每页显示的条目数
	private int pageSize;
	//当前现实的页数
	private int currPage;
	
	private String dbType;
	

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		//获取StatementHandler，默认是RoutingStatementHandler
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		//获取statementHandler包装类
		MetaObject metaObjectHandler = SystemMetaObject.forObject(statementHandler);
		//分离代理对象链
		while (metaObjectHandler.hasGetter("h")) {
			Object object = metaObjectHandler.getValue("h");
			metaObjectHandler = SystemMetaObject.forObject(object);
		}
		while (metaObjectHandler.hasGetter("target")) {
			Object object = metaObjectHandler.getValue("target");
			metaObjectHandler = SystemMetaObject.forObject(object);
		}
		//获取查询接口映射的相关信息
		MappedStatement mappedStatement = (MappedStatement) metaObjectHandler.getValue("delegate.mappedStatement");
		String mapId = mappedStatement.getId();
		//拦截以.ByPage结尾的请求，分页功能的统一实现
		if (mapId.matches(".+ByPage$")) {
			//获取进行数据库操作时管理参数的handler
			ParameterHandler parameterHandler = (ParameterHandler) metaObjectHandler.getValue("delegate.parameterHandler");
			//获取请求时的参数
			Map<String, Object> paraObject = (Map<String, Object>) parameterHandler.getParameterObject();
			//参数名称和在service中设置到map中的名称一致
            currPage = (int) paraObject.get("currPage");
            pageSize = (int) paraObject.get("pageSize");
			
            String sql = (String) metaObjectHandler.getValue("delegate.boundSql.sql");
			
            //构建分页功能的sql语句
            String limitSql;
            sql = sql.trim();
            limitSql = sql+" limit "+(currPage-1)*pageSize+","+pageSize;
            
            //将构建完成的分页sql语句赋值个体'delegate.boundSql.sql'，偷天换日
            metaObjectHandler.setValue("delegate.boundSql.sql", limitSql);
			
		}
		//调用原对象的方法，进入责任链的下一级
		return invocation.proceed();
	}

	//获取代理对象
	@Override
	public Object plugin(Object target) {
		//生成object对象的动态代理对象
        return Plugin.wrap(target, this);
	}

	//设置代理对象的参数
	@Override
	public void setProperties(Properties properties) {
		//如果项目中分页的pageSize是统一的，也可以在这里统一配置和获取，这样就不用每次请求都传递pageSize参数了。参数是在配置拦截器时配置的。
		String limit1 = properties.getProperty("limit", "10");
        this.pageSize = Integer.valueOf(limit1);
        this.dbType = properties.getProperty("dbType", "mysql");
	}

}
