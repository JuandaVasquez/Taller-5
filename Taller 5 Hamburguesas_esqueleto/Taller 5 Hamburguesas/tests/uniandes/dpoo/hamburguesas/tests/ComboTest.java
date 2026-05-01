package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest {
	
	private ProductoMenu hamburguesa;
    private ProductoMenu papas;
    private ProductoMenu gaseosa;

    // Escenario 1: combo normal con 10% de descuento
    private Combo comboConDescuento;

    // Escenario 2: combo sin descuento (0%)
    private Combo comboSinDescuento;

    // Escenario 3: combo de un solo producto
    private Combo comboUnProducto;

    @BeforeEach
    void setUp( ) throws Exception
    {
        hamburguesa = new ProductoMenu( "hamburguesa sencilla", 14000 );
        papas = new ProductoMenu( "papas medianas", 5500 );
        gaseosa = new ProductoMenu( "gaseosa", 5000 );

        ArrayList<ProductoMenu> items = new ArrayList<>( );
        items.add( hamburguesa );
        items.add( papas );
        items.add( gaseosa );

        // Escenario 1
        comboConDescuento = new Combo( "combo corral", 0.10, items );

        // Escenario 2
        ArrayList<ProductoMenu> items2 = new ArrayList<>( );
        items2.add( hamburguesa );
        items2.add( papas );
        items2.add( gaseosa );
        comboSinDescuento = new Combo( "combo sin descuento", 0.0, items2 );

        // Escenario 3
        ArrayList<ProductoMenu> items3 = new ArrayList<>( );
        items3.add( hamburguesa );
        comboUnProducto = new Combo( "combo solo", 0.05, items3 );
    }

    @AfterEach
    void tearDown( ) throws Exception
    {
        comboConDescuento = null;
        comboSinDescuento = null;
        comboUnProducto = null;
    }

    // ---- getNombre ----

    @Test
    void testGetNombreComboConDescuento( )
    {
        assertEquals( "combo corral", comboConDescuento.getNombre( ),
                "El nombre del combo no es el esperado." );
    }

    @Test
    void testGetNombreComboDiferente( )
    {
        assertEquals( "combo solo", comboUnProducto.getNombre( ),
                "El nombre del combo de un producto no es el esperado." );
    }

    // ---- getPrecio ----

    @Test
    void testGetPrecioConDescuento10Porciento( )
    {
        // Suma: 14000 + 5500 + 5000 = 24500; con 10% de descuento: 24500 * 0.9 = 22050
        assertEquals( 22050, comboConDescuento.getPrecio( ),
                "El precio del combo con 10% de descuento no es correcto. "
                        + "Recuerde que el descuento se RESTA (precio * (1 - descuento))." );
    }

    @Test
    void testGetPrecioSinDescuento( )
    {
        // Sin descuento el precio es igual a la suma de los productos
        assertEquals( 24500, comboSinDescuento.getPrecio( ),
                "El precio del combo sin descuento debe ser igual a la suma de sus productos." );
    }

    @Test
    void testGetPrecioUnProductoConDescuento( )
    {
        // 14000 * (1 - 0.05) = 14000 * 0.95 = 13300
        assertEquals( 13300, comboUnProducto.getPrecio( ),
                "El precio del combo de un solo producto con descuento del 5% no es correcto." );
    }

    @Test
    void testGetPrecioMenorQueSumaProductos( )
    {
        int sumaProductos = hamburguesa.getPrecio( ) + papas.getPrecio( ) + gaseosa.getPrecio( );
        assertTrue( comboConDescuento.getPrecio( ) < sumaProductos,
                "El precio del combo con descuento debe ser menor que la suma de sus productos." );
    }

    // ---- generarTextoFactura ----

    @Test
    void testGenerarTextoFacturaContieneNombre( )
    {
        String texto = comboConDescuento.generarTextoFactura( );
        assertTrue( texto.contains( "combo corral" ),
                "El texto de factura del combo debe contener el nombre del combo." );
    }

    @Test
    void testGenerarTextoFacturaContieneDescuento( )
    {
        String texto = comboConDescuento.generarTextoFactura( );
        assertTrue( texto.contains( "Descuento:" ),
                "El texto de factura del combo debe indicar el descuento." );
        assertTrue( texto.contains( "0.1" ),
                "El texto de factura del combo debe mostrar el valor del descuento." );
    }

    @Test
    void testGenerarTextoFacturaContienePrecio( )
    {
        String texto = comboConDescuento.generarTextoFactura( );
        assertTrue( texto.contains( "22050" ),
                "El texto de factura debe contener el precio final del combo." );
    }

    @Test
    void testGenerarTextoFacturaEstructura( )
    {
        // La factura debe empezar con "Combo "
        String texto = comboConDescuento.generarTextoFactura( );
        assertTrue( texto.startsWith( "Combo " ),
                "El texto de factura del combo debe comenzar con 'Combo '." );
    }
}
