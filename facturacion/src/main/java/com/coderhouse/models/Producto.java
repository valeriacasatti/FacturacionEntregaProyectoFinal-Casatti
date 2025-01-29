package com.coderhouse.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"ventaProductos"})

@Schema(description = "Modelo que representa un producto disponible en la plataforma")

@Entity
@Table(name = "productos")
public class Producto {
	
	@Schema(description="ID del producto", requiredMode=Schema.RequiredMode.REQUIRED, example="1")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Schema(description="Nombre del producto", requiredMode=Schema.RequiredMode.REQUIRED, example="Remera")
	@Column(nullable=false)
	private String nombre;
	
	@Schema(description="Precio del producto", requiredMode=Schema.RequiredMode.REQUIRED, example="100")
	@Column(nullable=false)
	private int precio;
	
	@Schema(description="Cantidad de stock disponible del producto", requiredMode=Schema.RequiredMode.REQUIRED, example="5")
	@Column(nullable=false)
	private Integer stock;
	
	@Schema(description="Lista de ventas en las que aparece este producto", hidden = true)
	@OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<VentaProducto> ventaProductos = new ArrayList<>();
}