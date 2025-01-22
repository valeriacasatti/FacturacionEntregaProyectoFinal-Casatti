package com.coderhouse.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coderhouse.dtos.VentaDTO;
import com.coderhouse.dtos.ProductoDTO;
import com.coderhouse.models.Cliente;
import com.coderhouse.models.Producto;
import com.coderhouse.models.Venta;
import com.coderhouse.repositories.ClienteRepository;
import com.coderhouse.repositories.ProductoRepository;
import com.coderhouse.repositories.VentaRepository;

import jakarta.transaction.Transactional;

@Service
public class VentaService {

	@Autowired
	private VentaRepository ventaRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ProductoRepository productoRepository;
	
	// GET ALL VENTAS
	public List<VentaDTO> getAllVentas() {
	    return ventaRepository.findAll().stream()
	        .map(venta -> new VentaDTO(
	            venta.getId(),
	            venta.getCliente().getId(),
	            venta.getCliente().getNombre(),
	            venta.getCliente().getApellido(),
	            venta.getTotal(),
	            venta.getFecha(),
	            venta.getProductos().stream()
	                .map(producto -> new ProductoDTO(producto.getId(), producto.getNombre()))
	                .toList()
	        ))
	        .toList();
	}
	
	// GET VENTA BY ID
	public VentaDTO getVentaById(Long id) {
	    Venta venta = ventaRepository.findById(id)
	        .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

	    return new VentaDTO(
	        venta.getId(),
	        venta.getCliente().getId(),
	        venta.getCliente().getNombre(),
	        venta.getCliente().getApellido(),
	        venta.getTotal(),
	        venta.getFecha(),
	        venta.getProductos().stream()
	            .map(producto -> new ProductoDTO(producto.getId(), producto.getNombre()))
	            .toList()
	    );
	}
	
	// CREAR VENTA/COMPROBANTE
	@Transactional
	public VentaDTO newVenta(Long clienteId, List<Long> productosId) {
		// Validar que el cliente exista
	    Cliente cliente = clienteRepository.findById(clienteId)
	            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
	    
	    // Validar que los productos existan
	    List<Producto> productos = new ArrayList<>();
	    
	    for(Long productoId : productosId) {
	    	Producto producto = productoRepository.findById(productoId)
	    			.orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
	    	
	    	// Validar stock
	    	if(producto.getStock() <= 0) {
	    		throw new IllegalArgumentException("Stock insuficiente");
	    	}
	    	
	    	// Reducir stock
	    	producto.setStock(producto.getStock() - 1);
	    	productoRepository.save(producto);
	    	
	    	productos.add(producto);
	    }
	    
	    // Calcular el total de la venta
	    int total = productos.stream()
	            .mapToInt(Producto::getPrecio)
	            .sum();
	    
	    Venta nuevaVenta = new Venta();
	    
	    nuevaVenta.setFecha(new Date(System.currentTimeMillis()));
	    nuevaVenta.setCliente(cliente);
	    nuevaVenta.setProductos(productos);
	    nuevaVenta.setTotal(total);
	    
		Venta ventaGuardada = ventaRepository.save(nuevaVenta);
		
		return new VentaDTO(
			    ventaGuardada.getId(),
			    cliente.getId(),
			    cliente.getNombre(),
			    cliente.getApellido(),
			    ventaGuardada.getTotal(),
			    ventaGuardada.getFecha(),
			    productos.stream()
			        .map(producto -> new ProductoDTO(producto.getId(), producto.getNombre()))
			        .toList()
			);
	}
	
	// ACTUALIZAR VENTA
	@Transactional
	public VentaDTO updateVentaById(Long id, VentaDTO dto) {
		Venta venta = ventaRepository.findById(id)
				.orElseThrow(()-> new IllegalArgumentException("Venta no encontrada"));
		
		// Actualizar cliente
	    if (dto.getClienteId() != null) {
	        Cliente cliente = clienteRepository.findById(dto.getClienteId())
	                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
	        venta.setCliente(cliente);
	    }
	    
	    // Actualizar productos
	    if (dto.getProductos() != null && !dto.getProductos().isEmpty()) {
	    	
	    	// Restaurar el stock
	        for (Producto producto : venta.getProductos()) {
	        	if (producto.getStock() == null) {
	                throw new IllegalArgumentException("Stock nulo para producto: " + producto.getId());
	            }
	            producto.setStock(producto.getStock() + 1);
	            productoRepository.save(producto);
	        }
	        
	        // Validar y agregar los nuevos productos
	        List<Producto> nuevosProductos = new ArrayList<>();
	        for (ProductoDTO productoDTO : dto.getProductos()) {
	            Producto producto = productoRepository.findById(productoDTO.getId())
	                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
	            if (producto.getStock() <= 0) {
	                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
	            }
	            producto.setStock(producto.getStock() - 1); // Reducir el stock
	            productoRepository.save(producto);
	            nuevosProductos.add(producto);
	        }

	        	venta.getProductos().clear(); 
	        	venta.getProductos().addAll(nuevosProductos);
	        
	        // Recalcular el total
	        int nuevoTotal = nuevosProductos.stream()
	                .mapToInt(Producto::getPrecio)
	                .sum();
	        venta.setTotal(nuevoTotal);
	    }
	    
	    // Guardar la venta actualizada
	    Venta ventaActualizada = ventaRepository.save(venta);
	    
	    return new VentaDTO(
	            ventaActualizada.getId(),
	            ventaActualizada.getCliente().getId(),
	            ventaActualizada.getCliente().getNombre(),
	            ventaActualizada.getCliente().getApellido(),
	            ventaActualizada.getTotal(),
	            ventaActualizada.getFecha(),
	            ventaActualizada.getProductos().stream()
	                    .map(producto -> new ProductoDTO(producto.getId(), producto.getNombre()))
	                    .toList()
	    );
	}
	
	// ELIMINAR VENTA
	public void deleteVentaById(Long id) {
		if(!ventaRepository.existsById(id)) {
			throw new IllegalArgumentException("Venta no encontrada");
		}
		ventaRepository.deleteById(id);
	}
}