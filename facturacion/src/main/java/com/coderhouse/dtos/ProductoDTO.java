package com.coderhouse.dtos;

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

@Schema(description = "Modelo que representa un producto dentro de una venta")
public class ProductoDTO {

	@Schema(description="ID del producto", requiredMode=Schema.RequiredMode.REQUIRED, example="1")
	private Long id;
	
	@Schema(description="Nombre del producto", requiredMode=Schema.RequiredMode.REQUIRED, example="Remera")
    private String nombre;
	
	@Schema(description="Precio del producto", requiredMode=Schema.RequiredMode.REQUIRED, example="100")
    private int precio;
	
	@Schema(description="Cantidad de productos seleccionados", requiredMode=Schema.RequiredMode.REQUIRED, example="2")
    private Integer cantidad;
}
