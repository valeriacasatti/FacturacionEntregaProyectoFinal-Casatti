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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@ToString(exclude = {"cliente", "productos"})

@Entity
@Table(name = "ventas")
@Schema(description = "Modelo que representa una venta en la plataforma")
public class Venta {

	@Schema(description="ID de la venta", requiredMode=Schema.RequiredMode.REQUIRED, example="1")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Schema(description="Fecha en la que se realizó la venta", requiredMode=Schema.RequiredMode.REQUIRED,
			format = "date", example="2025/01/17")
	@Column(nullable = false)
	private String fecha;
	
	@Schema(description="Monto total de la venta", requiredMode=Schema.RequiredMode.REQUIRED, example="500")
	@Column(nullable = false)
	private Integer total;
	
	@Schema(description="ID del cliente asociado a la venta", requiredMode=Schema.RequiredMode.REQUIRED, example="1")
	@ManyToOne
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;
	
	@Schema(description="Lista de productos asociados a la venta", example = "[{venta_id: 1, producto_id: 2}]", hidden = true)
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "venta_producto",
			joinColumns = @JoinColumn(name = "venta_id"),
			inverseJoinColumns = @JoinColumn(name = "producto_id")
			)
	@JsonIgnore
	private List<Producto> productos = new ArrayList<>();
}