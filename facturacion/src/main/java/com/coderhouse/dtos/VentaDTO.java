package com.coderhouse.dtos;

import java.util.List;
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

public class VentaDTO {

	private Long id;
	private Long clienteId;
	private String clienteNombre;
	private String clienteApellido;
    private String fecha;
    private List<ProductoDTO> productos;
    private Integer total;
    
}