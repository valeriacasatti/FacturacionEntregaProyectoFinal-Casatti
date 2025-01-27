package com.coderhouse.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@ToString(exclude = {"ventas"})

@Schema(description = "Modelo que representa a un cliente en la plataforma")

@Entity
@Table(name = "clientes")
public class Cliente {

	@Schema(description="ID del cliente", requiredMode=Schema.RequiredMode.REQUIRED, example="1")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Schema(description="Nombre del cliente", requiredMode=Schema.RequiredMode.REQUIRED, example="Valeria")
	@Column(nullable=false)
	private String nombre;
	
	@Schema(description="Apellido del cliente", requiredMode=Schema.RequiredMode.REQUIRED, example="Casatti")
	@Column(nullable=false)
	private String apellido;
	
	@Schema(description="Email del cliente", requiredMode=Schema.RequiredMode.REQUIRED,
			format = "email", example="valeria@gmail.com")
	@Column(unique=true,nullable=false)
	private String email;
	
	@Schema(description="Lista de ventas realizadas por el cliente", hidden = true)
	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Venta> ventas = new ArrayList<>();
}