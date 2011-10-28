package br.com.opensig.comercial.server;

import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

public class TipoDouble implements TypeHandler {

	@Override
	public Object parse(String text) throws TypeConversionException {
		return Double.parseDouble(text) / 100;
	}

	@Override
	public String format(Object value) {
		double valor = 0.00;
		if (value != null) {
			valor = Double.parseDouble(value.toString()) / 100;
		}
		return valor + "";
	}

	@Override
	public Class<?> getType() {
		return Double.class;
	}

}