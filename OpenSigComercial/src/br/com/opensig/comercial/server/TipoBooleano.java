package br.com.opensig.comercial.server;

import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

public class TipoBooleano implements TypeHandler {

	@Override
	public Object parse(String text) throws TypeConversionException {
		return "S".equals(text);
	}

	@Override
	public String format(Object value) {
		return value != null && ((Boolean)value).booleanValue() ? "S" : "N";
	}

	@Override
	public Class<?> getType() {
		return Boolean.class;
	}

}