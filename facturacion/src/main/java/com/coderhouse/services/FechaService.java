package com.coderhouse.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.coderhouse.dtos.FechaDTO;
import com.coderhouse.models.Venta;

@Service
public class FechaService {

	@Autowired
	private RestTemplate restTemplate;
	
	// Formato de fecga
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	public void asignarFecha(Venta venta) {
		try {
			// URL
			final String URL = "https://timeapi.io/api/Time/current/zone?timeZone=America/Argentina/Buenos_Aires";
			// Obtener la fecha de la API
    		FechaDTO fechaDTO = restTemplate.getForObject(URL, FechaDTO.class);
    		
    		// Validar si la API respondio correctamente
    		if(fechaDTO != null && fechaDTO.getDate() != null && fechaDTO.getTime() != null) {
    			String fechaString = String.format("%s %s", fechaDTO.getDate(), fechaDTO.getTime());
    			venta.setFecha(fechaString);
    		}else {
    			// Asignar fecha local
    			venta.setFecha(obtenerFechaLocal());
    		}
			
		} catch(RestClientException e) {
			System.err.println("Error, no se pudo conectar a la API externa: " + e.getMessage());
		}
	}
	
	// Metodo para obtener fecha local en formato String
	private String obtenerFechaLocal() {
		return LocalDateTime.now().format(DATE_FORMAT);
	}
	
}
