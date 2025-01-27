package com.coderhouse.dtos;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Schema(description = "Modelo que representa una venta realizada en la plataforma")
public class VentaDTO {

	@Schema(description="ID de la venta", requiredMode=Schema.RequiredMode.REQUIRED, example="1")
	private Long id;
	
	@Schema(description="ID del cliente asociado a la venta", requiredMode=Schema.RequiredMode.REQUIRED, example="1")
	private Long clienteId;
	
	@Schema(description="Nombre del cliente asociado a la venta", requiredMode=Schema.RequiredMode.REQUIRED, example="Valeria")
	private String clienteNombre;
	
	@Schema(description="Apellido del cliente asociado a la venta", requiredMode=Schema.RequiredMode.REQUIRED, example="Casatti")
	private String clienteApellido;
	
	@Schema(description="Fecha en la que se realizó la venta", requiredMode=Schema.RequiredMode.REQUIRED,
			format = "date", example="2025/01/17")
    private String fecha;
	
	@Schema(description="Lista de productos asociados a la venta", example = "[{id: 1, nombre: 'Remera', precio: 100}]")
    private List<ProductoDTO> productos;
    
	@Schema(description="Monto total de la venta", requiredMode=Schema.RequiredMode.REQUIRED, example="500")
    private Integer total;
    
}