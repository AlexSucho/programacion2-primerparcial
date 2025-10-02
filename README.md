Nombre de la aplicacion: Inventario de Videojuegos.

Funciones principales:

*Inventario (Videojuegos)

Crear, listar, editar y eliminar videojuegos (CRUD).

Campos: título, precio, stock y categoría.

Actualización inmediata de la lista tras cada operación.

*Categorías

Alta, edición y eliminación de categorías.

Asociación de videojuegos a una categoría.

*Clientes

Gestión de clientes (CRUD) con datos básicos de contacto.

*Ventas

Registrar venta seleccionando cliente, videojuego y cantidad.

Control automático de stock: descuenta al vender; repone al editar o eliminar la venta.

Visualización de fecha, cliente e ítems totales por venta.

*Navegación y UI

BottomNavigation para moverse entre Inventario, Clientes, Ventas y Ajustes.

Formularios en diálogos para altas/ediciones y listas con adaptadores.

*Persistencia

Base de datos local con Room; incluye datos semilla de categorías.

*Estabilidad

Operaciones en segundo plano y manejo del ciclo de vida para evitar cierres al cambiar de pestañas.

Objetivo: brindar una herramienta clara y rápida para controlar existencias, registrar ventas y mantener organizada la información de la tienda.
