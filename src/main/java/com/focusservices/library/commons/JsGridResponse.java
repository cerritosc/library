package com.focusservices.library.commons;

import java.util.List;

/**
 * Clase generica que representa el response que utiliza JsGrid para popular la tabla.
 * Maneja el parametro <b>itemsCount</b>, que representa la cantidad de filas y
 * el parametro <b>data</b>, el cual es una lista generica donde se encontrara la coleccion
 * de entidades a devolver a la vista.
 * @author jlozano 
 *         
 */
public class JsGridResponse {
	
	private Long itemsCount;
	
	private List<? extends Object> data;
	
	public JsGridResponse(List<? extends Object> data, Long itemsCount) {
		this.data = data;
		this.itemsCount = itemsCount;
	}

	public Long getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(Long itemsCount) {
		this.itemsCount = itemsCount;
	}

	public List<? extends Object> getData() { //NOSONAR
		return data;
	}

	public void setData(List<? extends Object> data) {
		this.data = data;
	}

	
}
