package com.example.apptienda.modelos;

import java.util.Date;

/**
 * Modelo mínimo para una venta.
 */
public class modeloVenta {
	private int id;
	private int productoId;
	private int cantidad;
	private double total;
	private Date fecha;

	public Sale(int id, int productoId, int cantidad, double total, Date fecha) {
		this.id = id;
		this.productoId = productoId;
		this.cantidad = cantidad;
		this.total = total;
		this.fecha = fecha;
	}

	public int getId() { 
		return id; 
	}

	public int getProductoId() { 
		return productoId; 
	}

	public int getCantidad() {
		 return cantidad; 
	}

	public double getTotal() { 
		return total; 
	}

	public Date getDate() { 
		return fecha
		; 
	}

	public void setId(int id) { 
		this.id = id; 
	}

	public void setProductId(int productoId) { 
		this.productoId = productoId; 
	}

	public void setQuantity(int cantidad) { 
		this.cantidad = cantidad; 
	}

	public void setTotal(double total) { 
		this.total = total; 
	}

	public void setDate(Date fecha) { 
		this.fecha = fecha; 
	}
}
