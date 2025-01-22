package com.coderhouse.dtos;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class VentaDTO {

	private Long id;
    private Date fecha;
    private Integer total;
	private Long clienteId;
    private List<Long> productosId;
    
}