package com.coderhouse.dtos;

import java.sql.Date;
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
    private Date fecha;
    private Integer total;
	private Long clienteId;
    private List<Long> productosId;
    
}