package net.kaciras.blog.infra.codec;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.Mockito.mock;

abstract class AbstractTypeHandlerTest {

	ResultSet resultSet = mock(ResultSet.class);
	PreparedStatement preparedStatement = mock(PreparedStatement.class);
	CallableStatement callableStatement = mock(CallableStatement.class);

	abstract void setParameter() throws Exception;

	// (ResultSet|CallableStatement) 的 getXXX 没法抽象，只能一个个写

	abstract void getResultFromResultSetByName() throws Exception;

	abstract void getResultFromResultSetByPosition() throws Exception;

	abstract void getResultFromCallableStatement() throws Exception;
}
