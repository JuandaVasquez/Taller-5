package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoMenuTest {
    // ============================================================
    // Atributos de los escenarios
    // ============================================================
 
    // Escenario 1: producto ajustado recién creado, sin modificaciones
    private ProductoAjustado sinModificaciones;
 
    // Escenario 2: producto ajustado con un producto base de precio alto
    private ProductoAjustado productoCaroAjustado;
 
    // Productos base
    private ProductoMenu base;
    private ProductoMenu baseCaro;
 
    // ============================================================
    // Configuración
    // ============================================================
 
    @BeforeEach
    void setUp( )
    {
        base      = new ProductoMenu( "corral", 14000 );
        baseCaro  = new ProductoMenu( "1/2 libra", 25000 );
 
        sinModificaciones    = new ProductoAjustado( base );
        productoCaroAjustado = new ProductoAjustado( baseCaro );
    }
 
    @AfterEach
    void tearDown( )
    {
        sinModificaciones    = null;
        productoCaroAjustado = null;
    }
 
    // ============================================================
    // Pruebas de getNombre()
    // ============================================================
 
    @Test
    void testGetNombreMismoQueBase( )
    {
        assertEquals( "corral", sinModificaciones.getNombre( ),
                "El nombre debe ser el mismo que el del producto base." );
    }
 
    @Test
    void testGetNombreProductoCaroMismoQueBase( )
    {
        assertEquals( "1/2 libra", productoCaroAjustado.getNombre( ),
                "El nombre debe coincidir con el del producto base caro." );
    }
 
    // ============================================================
    // Pruebas de getPrecio()
    // ============================================================
 
    @Test
    void testGetPrecioSinModificacionesIgualAlBase( )
    {
        // BUG: el esqueleto retorna siempre 0
        // El valor correcto es el precio del producto base: 14000
        assertEquals( 14000, sinModificaciones.getPrecio( ),
                "BUG en getPrecio(): sin modificaciones debe retornar el precio base (14000)." );
    }
 
    @Test
    void testGetPrecioProductoCaroSinModificaciones( )
    {
        // BUG: el esqueleto retorna siempre 0
        // El valor correcto es el precio del producto base: 25000
        assertEquals( 25000, productoCaroAjustado.getPrecio( ),
                "BUG en getPrecio(): debe retornar el precio base (25000)." );
    }
 
    // ============================================================
    // Pruebas de generarTextoFactura()
    // ============================================================
 
    @Test
    void testFacturaContieneNombreDelProductoBase( )
    {
        String texto = sinModificaciones.generarTextoFactura( );
        // BUG: el esqueleto llama sb.append(productoBase) en vez de
        //      sb.append(productoBase.generarTextoFactura())
        assertTrue( texto.contains( "corral" ),
                "BUG en generarTextoFactura(): debe llamar productoBase.generarTextoFactura()." );
    }
 
    @Test
    void testFacturaContienePrecioBase( )
    {
        String texto = sinModificaciones.generarTextoFactura( );
        // BUG: el precio es 0 en el esqueleto; cuando se corrija debe aparecer 14000
        assertTrue( texto.contains( "14000" ),
                "BUG en generarTextoFactura(): debe mostrar el precio correcto (14000)." );
    }
 
    @Test
    void testFacturaNoEsNulaNiVacia( )
    {
        String texto = sinModificaciones.generarTextoFactura();
        assertNotNull( texto, "El texto de factura no debe ser null." );
        assertFalse( texto.isEmpty( ), "El texto de factura no debe estar vacío." );
    }
 
    @Test
    void testFacturaProductoCaroContieneNombre( )
    {
        String texto = productoCaroAjustado.generarTextoFactura( );
        assertTrue( texto.contains( "1/2 libra" ),
                "La factura debe contener el nombre del producto base caro." );
    }
 
    @Test
    void testFacturaSinModificacionesNoTieneSignoMas( )
    {
        String texto = sinModificaciones.generarTextoFactura( );
        assertFalse( texto.contains( "+" ),
                "Sin ingredientes agregados la factura no debe mostrar '+'." );
    }
 
    @Test
    void testFacturaSinModificacionesNoTieneSignoMenos( )
    {
        String texto = sinModificaciones.generarTextoFactura( );
        assertFalse( texto.contains( "-" ),
                "Sin ingredientes eliminados la factura no debe mostrar '-'." );
    }
}
