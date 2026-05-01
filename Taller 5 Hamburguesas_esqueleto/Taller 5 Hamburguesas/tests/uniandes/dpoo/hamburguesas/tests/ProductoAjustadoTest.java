package uniandes.dpoo.hamburguesas.tests;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoAjustadoTest {

    // ============================================================
    // Atributos de los escenarios
    // ============================================================

    /** Producto base del menú */
    private ProductoMenu productoBase;

    /** Ingredientes de prueba */
    private Ingrediente queso;
    private Ingrediente tomate;
    private Ingrediente cebolla;

    /** Escenario 1: producto ajustado sin modificaciones */
    private ProductoAjustado sinModificaciones;

    /** Escenario 2: producto con ingrediente agregado */
    private ProductoAjustado conIngredienteAgregado;

    /** Escenario 3: producto con ingrediente eliminado */
    private ProductoAjustado conIngredienteEliminado;

    /** Escenario 4: producto con varios ingredientes agregados y eliminados */
    private ProductoAjustado conVariasModificaciones;

    // ============================================================
    // Configuración
    // ============================================================

    /**
     * Configura los escenarios antes de cada prueba.
     */
    @BeforeEach
    public void setUp() {
        productoBase = new ProductoMenu("corral", 14000);
        queso        = new Ingrediente("queso mozzarella", 2500);
        tomate       = new Ingrediente("tomate", 1000);
        cebolla      = new Ingrediente("cebolla", 1000);

        // Escenario 1: sin modificaciones
        sinModificaciones = new ProductoAjustado(productoBase);

        // Escenario 2: con un ingrediente agregado
        conIngredienteAgregado = new ProductoAjustado(productoBase);
        conIngredienteAgregado.agregarIngrediente(queso);

        // Escenario 3: con un ingrediente eliminado
        conIngredienteEliminado = new ProductoAjustado(productoBase);
        conIngredienteEliminado.eliminarIngrediente(tomate);

        // Escenario 4: con varias modificaciones
        conVariasModificaciones = new ProductoAjustado(productoBase);
        conVariasModificaciones.agregarIngrediente(queso);
        conVariasModificaciones.agregarIngrediente(cebolla);
        conVariasModificaciones.eliminarIngrediente(tomate);
    }

    // ============================================================
    // Pruebas de getNombre()
    // ============================================================

    /** El nombre debe ser el mismo que el producto base */
    @Test
    public void testGetNombreMismoQueBase() {
        assertEquals("corral", sinModificaciones.getNombre(),
                "El nombre debe coincidir con el producto base");
    }

    /** El nombre no cambia al agregar ingredientes */
    @Test
    public void testGetNombreNoMutaConIngredientes() {
        assertEquals("corral", conIngredienteAgregado.getNombre(),
                "El nombre no debe cambiar al agregar ingredientes");
    }

    // ============================================================
    // Pruebas de getPrecio()
    // ============================================================

    /** Sin modificaciones, el precio debe ser igual al precio base */
    @Test
    public void testGetPrecioSinModificaciones() {
        assertEquals(14000, sinModificaciones.getPrecio(),
                "Sin modificaciones el precio debe ser igual al base");
    }

    /** Agregar ingrediente aumenta el precio */
    @Test
    public void testGetPrecioConIngredienteAgregado() {
        int esperado = 14000 + 2500; // base + queso
        assertEquals(esperado, conIngredienteAgregado.getPrecio(),
                "El precio debe incluir el costo del ingrediente agregado");
    }

    /** Eliminar ingrediente NO reduce el precio */
    @Test
    public void testGetPrecioConIngredienteEliminadoNoBaja() {
        assertEquals(14000, conIngredienteEliminado.getPrecio(),
                "Eliminar ingredientes no debe reducir el precio");
    }

    /** Con varios ingredientes agregados el precio se acumula correctamente */
    @Test
    public void testGetPrecioConVariasModificaciones() {
        int esperado = 14000 + 2500 + 1000; // base + queso + cebolla (tomate eliminado no resta)
        assertEquals(esperado, conVariasModificaciones.getPrecio(),
                "El precio debe acumular todos los ingredientes agregados");
    }

    // ============================================================
    // Pruebas de agregarIngrediente() y getAgregados()
    // ============================================================

    /** Sin modificaciones la lista de agregados debe estar vacía */
    @Test
    public void testAgregadosVacioInicial() {
        assertTrue(sinModificaciones.getAgregados().isEmpty(),
                "La lista de agregados debe estar vacía inicialmente");
    }

    /** Después de agregar un ingrediente debe aparecer en la lista */
    @Test
    public void testAgregarIngredienteAparece() {
        assertTrue(conIngredienteAgregado.getAgregados().contains(queso),
                "El ingrediente agregado debe estar en la lista");
    }

    /** Puede haber varios ingredientes agregados */
    @Test
    public void testVariosIngredientesAgregados() {
        assertEquals(2, conVariasModificaciones.getAgregados().size(),
                "Deben haber 2 ingredientes agregados");
    }

    // ============================================================
    // Pruebas de eliminarIngrediente() y getEliminados()
    // ============================================================

    /** Sin modificaciones la lista de eliminados debe estar vacía */
    @Test
    public void testEliminadosVacioInicial() {
        assertTrue(sinModificaciones.getEliminados().isEmpty(),
                "La lista de eliminados debe estar vacía inicialmente");
    }

    /** Después de eliminar un ingrediente debe aparecer en la lista */
    @Test
    public void testEliminarIngredienteAparece() {
        assertTrue(conIngredienteEliminado.getEliminados().contains(tomate),
                "El ingrediente eliminado debe estar en la lista");
    }

    /** Puede haber varios ingredientes eliminados */
    @Test
    public void testVariosIngredientesEliminados() {
        assertEquals(1, conVariasModificaciones.getEliminados().size(),
                "Debe haber 1 ingrediente eliminado");
    }

    // ============================================================
    // Pruebas de generarTextoFactura()
    // ============================================================

    /** La factura debe contener el nombre del producto */
    @Test
    public void testFacturaContieneNombre() {
        String texto = sinModificaciones.generarTextoFactura();
        assertTrue(texto.contains("corral"),
                "La factura debe contener el nombre del producto");
    }

    /** La factura debe contener el precio */
    @Test
    public void testFacturaContienePrecio() {
        String texto = conIngredienteAgregado.generarTextoFactura();
        assertTrue(texto.contains(String.valueOf(14000 + 2500)),
                "La factura debe contener el precio con ingredientes");
    }

    /** La factura con ingrediente eliminado debe mencionar qué se quitó */
    @Test
    public void testFacturaMencionaEliminados() {
        String texto = conIngredienteEliminado.generarTextoFactura();
        assertTrue(texto.contains("tomate"),
                "La factura debe mencionar el ingrediente eliminado");
    }

    /** La factura con ingrediente agregado debe mencionar qué se puso */
    @Test
    public void testFacturaMencionaAgregados() {
        String texto = conIngredienteAgregado.generarTextoFactura();
        assertTrue(texto.contains("queso mozzarella"),
                "La factura debe mencionar el ingrediente agregado");
    }

    /** La factura sin modificaciones no debe decir 'Sin' ni 'Con' */
    @Test
    public void testFacturaSinModificacionesNoMencionaAjustes() {
        String texto = sinModificaciones.generarTextoFactura();
        assertFalse(texto.contains("Sin:"),
                "Sin eliminaciones la factura no debe decir 'Sin:'");
        assertFalse(texto.contains("Con:"),
                "Sin agregados la factura no debe decir 'Con:'");
    }

    /** La factura con varias modificaciones debe mencionarlas todas */
    @Test
    public void testFacturaConVariasModificaciones() {
        String texto = conVariasModificaciones.generarTextoFactura();
        assertTrue(texto.contains("queso mozzarella"), "Debe mencionar queso");
        assertTrue(texto.contains("cebolla"), "Debe mencionar cebolla");
        assertTrue(texto.contains("tomate"), "Debe mencionar tomate (eliminado)");
    }
}
