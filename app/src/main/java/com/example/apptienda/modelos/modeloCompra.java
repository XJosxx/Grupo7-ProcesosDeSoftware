package com.example.apptienda.modelos;

import java.util.Date;

/**
 * Modelo de compra para registrar reabastecimientos.
 */
public class modeloCompra {
	private long id;
	private long productoId;
	private Producto producto;
	private String rucProveedor;
	private String nombreProveedor;
	private double precioUnitario;
	private double cantidadComprada;
	private double total;
	private Date fechaCompra;

	public modeloCompra() {
		this.fechaCompra = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getProductoId() {
		return productoId;
	}

	public void setProductoId(long productoId) {
		this.productoId = productoId;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public String getRucProveedor() {
		return rucProveedor;
	}

	public void setRucProveedor(String rucProveedor) {
		this.rucProveedor = rucProveedor;
	}

	public String getNombreProveedor() {
		return nombreProveedor;
	}

	public void setNombreProveedor(String nombreProveedor) {
		this.nombreProveedor = nombreProveedor;
	}

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
		recalcularTotal();
	}

	public double getCantidadComprada() {
		return cantidadComprada;
	}

	public void setCantidadComprada(double cantidadComprada) {
		this.cantidadComprada = cantidadComprada;
		recalcularTotal();
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public Date getFechaCompra() {
		return fechaCompra;
	}

	public void setFechaCompra(Date fechaCompra) {
		this.fechaCompra = fechaCompra;
	}

	private void recalcularTotal() {
		this.total = Math.round(precioUnitario * cantidadComprada * 100.0d) / 100.0d;
	}
}
