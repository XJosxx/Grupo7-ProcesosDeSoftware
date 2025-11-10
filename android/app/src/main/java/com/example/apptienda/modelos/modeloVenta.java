package com.example.apptienda.modelos;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Modelo de venta con toda la informacion necesaria para la boleta.
 */
public class modeloVenta {

	public static final double DEFAULT_IGV = 0.18d;

	private long id;
	private String codigo;
	private long productoId;
	private String nombreProductoManual;
	private String dniComprador;
	private double cantidad;
	private double precioUnitario;
	private double costoUnitario;
	private double igvPorcentaje = DEFAULT_IGV;
	private double igvMonto;
	private double totalSinIgv;
	private double totalConIgv;
	private Date fecha;

	public modeloVenta() {
		this.codigo = generarCodigoTemporal();
		this.fecha = new Date();
	}

	public modeloVenta(long id, long productoId, double cantidad, double precioUnitario, Date fecha) {
		this();
		this.id = id;
		this.productoId = productoId;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.fecha = fecha != null ? fecha : new Date();
		recalcularTotales();
	}

	private String generarCodigoTemporal() {
		return "TMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
	}

	public void recalcularTotales() {
		totalSinIgv = redondear2Decimales(cantidad * precioUnitario);
		igvMonto = redondear2Decimales(totalSinIgv * igvPorcentaje);
		totalConIgv = redondear2Decimales(totalSinIgv + igvMonto);
	}

	private double redondear2Decimales(double valor) {
		return Math.round(valor * 100.0d) / 100.0d;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public long getProductoId() {
		return productoId;
	}

	public void setProductoId(long productoId) {
		this.productoId = productoId;
	}

	public String getNombreProductoManual() {
		return nombreProductoManual;
	}

	public void setNombreProductoManual(String nombreProductoManual) {
		this.nombreProductoManual = nombreProductoManual;
	}

	public String getDniComprador() {
		return dniComprador;
	}

	public void setDniComprador(String dniComprador) {
		this.dniComprador = dniComprador;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
		recalcularTotales();
	}

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
		recalcularTotales();
	}

	public double getCostoUnitario() {
		return costoUnitario;
	}

	public void setCostoUnitario(double costoUnitario) {
		this.costoUnitario = costoUnitario;
	}

	public double getIgvPorcentaje() {
		return igvPorcentaje;
	}

	public void setIgvPorcentaje(double igvPorcentaje) {
		this.igvPorcentaje = igvPorcentaje;
		recalcularTotales();
	}

	public double getIgvMonto() {
		return igvMonto;
	}

	public void setIgvMonto(double igvMonto) {
		this.igvMonto = igvMonto;
	}

	public double getTotalSinIgv() {
		return totalSinIgv;
	}

	public void setTotalSinIgv(double totalSinIgv) {
		this.totalSinIgv = totalSinIgv;
	}

	public double getTotalConIgv() {
		return totalConIgv;
	}

	public void setTotalConIgv(double totalConIgv) {
		this.totalConIgv = totalConIgv;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getTotal() {
		return totalConIgv;
	}

	public boolean tieneProductoRegistrado() {
		return productoId > 0;
	}

	public double getCantidadParaStock() {
		return Math.ceil(cantidad);
	}
}
