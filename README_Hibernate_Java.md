# Guía de Hibernate en Java: Models, relaciones y CRUD

> Esta guía sigue el estilo de los ejemplos de clase: entidades Java con
> tres constructores y Services que trabajan con `Session` y
> `Transaction`, usando la API clásica de Hibernate (`save`, `get`,
> `update`, `delete`).

## Índice

1.  [Qué es un Model](#1-qué-es-un-model)
2.  [Anotaciones de una entidad](#2-anotaciones-de-una-entidad)
3.  [Dónde van las anotaciones](#3-dónde-van-las-anotaciones)
4.  [Relaciones y lado propietario](#4-relaciones-y-lado-propietario)
5.  [Los tres constructores](#5-los-tres-constructores)
6.  [Getters, setters y sincronización de
    relaciones](#6-getters-setters-y-sincronización-de-relaciones)
7.  [CRUD con Hibernate](#7-crud-con-hibernate)
8.  [CRUD con relaciones: ejemplo
    Pokemon](#8-crud-con-relaciones-ejemplo-pokemon)
9.  [Chuleta para estudiar](#9-chuleta-para-estudiar)

------------------------------------------------------------------------

## 1. Qué es un Model

Un **Model** (también llamado entidad o `Entity`) es una clase Java que
representa una entidad que Hibernate puede guardar en la base de datos.

Ejemplo:

-   Clase Java: `Pokemon`
-   Tabla: `pokemon`
-   Atributos Java: `id`, `nome`, `adestrador`, `pokedex`
-   Columnas: `id`, `nome`, `adestrador`, `pokedexentry`

Hibernate relaciona los objetos Java con las filas de las tablas. Así
podemos trabajar con objetos en lugar de escribir SQL para cada
operación.

## 2. Anotaciones de una entidad

### `@Entity`

Se coloca encima de la declaración de la clase. Le indica a Hibernate
que debe gestionar esa clase como una entidad persistente.

``` java
@Entity
public class Pokemon {
}
```

### `@Table(name = "...")`

También se coloca encima de la clase. Permite indicar el nombre de la
tabla de la base de datos.

``` java
@Entity
@Table(name = "pokemon")
public class Pokemon {
}
```

`Pokemon` es el nombre de la clase Java y `pokemon` es el nombre de la
tabla. Si no se especifica `@Table`, Hibernate puede deducir un nombre a
partir de la clase y de su configuración.

### `@Id`

Se coloca justo encima del atributo que será la clave primaria de la
tabla.

``` java
@Id
private Long id;
```

La clave primaria identifica de forma única cada registro.

### `@GeneratedValue`

Se coloca junto a `@Id` cuando queremos que la base de datos genere el
identificador automáticamente.

``` java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

Con `GenerationType.IDENTITY`, normalmente la base de datos genera el ID
al insertar la fila. Por eso, al crear un objeto nuevo, normalmente
usamos el constructor **sin ID**.

### `@Column(...)`

Se coloca encima de un atributo normal, es decir, uno que representa una
columna y no una relación con otra entidad.

``` java
@Column(name = "nome", nullable = false, length = 255)
private String nome;
```

Parámetros habituales:

-   `name`: nombre de la columna en la base de datos.
-   `nullable = false`: la columna no admite `NULL`.
-   `nullable = true`: la columna permite `NULL`.
-   `length = 255`: longitud para columnas de texto, por ejemplo
    `String`.

Ejemplos:

``` java
@Column(name = "nome", nullable = false, length = 255)
private String nome;

@Column(name = "peso", nullable = false)
private Double peso;
```

**Importante:** `@Column` se utiliza para columnas normales. Para
configurar la columna que guarda una clave foránea en una relación se
suele utilizar `@JoinColumn`.

### `@JoinColumn(name = "...")`

Se coloca encima del atributo que representa una relación, normalmente
en el lado propietario.

``` java
@ManyToOne
@JoinColumn(name = "adestrador")
private Adestrador adestrador;
```

En este ejemplo, `pokemon.adestrador` es una columna de la tabla
`pokemon` que guarda el ID del adiestrador relacionado.

`name` es el nombre de la columna de la clave foránea en la base de
datos. No es el nombre del atributo Java ni el nombre de la clase.

Se puede añadir, por ejemplo, `nullable = true` o `nullable = false`:

``` java
@JoinColumn(name = "adestrador", nullable = true)
```

-   `nullable = true`: el Pokémon puede no tener adiestrador.
-   `nullable = false`: el Pokémon debe tener adiestrador.

### Diferencia rápida: `@Column` y `@JoinColumn`

  -----------------------------------------------------------------------
  Anotación                           Se usa para
  ----------------------------------- -----------------------------------
  `@Column`                           Una columna normal, como `nome`,
                                      `peso` o `misc`.

  `@JoinColumn`                       La columna que guarda la clave
                                      foránea de una relación.
  -----------------------------------------------------------------------

No se suele poner `@Column` encima de un atributo que ya está mapeado
como relación con `@ManyToOne` o `@OneToOne`.

## 3. Dónde van las anotaciones

La posición depende de qué se está configurando:

``` java
@Entity                         // La clase es una entidad
@Table(name = "pokemon")        // Nombre de la tabla
public class Pokemon {

    @Id                         // Clave primaria
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(name = "nome", nullable = false) // Columna normal
    private String nome;

    @ManyToOne                   // Relación: muchos Pokemon con un Adestrador
    @JoinColumn(name = "adestrador") // FK en la tabla pokemon
    private Adestrador adestrador;
}
```

Regla para decidir:

1.  ¿Es la clase? Usa `@Entity` y, si quieres fijar el nombre de la
    tabla, `@Table`.
2.  ¿Es el identificador? Usa `@Id` y, si se genera automáticamente,
    `@GeneratedValue`.
3.  ¿Es un dato simple (`String`, `Double`, `Integer`, etc.)? Usa
    `@Column` si necesitas configurar su columna.
4.  ¿Es una referencia a otra entidad? Usa la anotación de relación
    (`@ManyToOne`, `@OneToOne`, etc.) y, si este lado guarda la FK,
    `@JoinColumn`.
5.  ¿Es el lado inverso de una relación bidireccional? Usa `mappedBy` en
    la anotación de relación.

------------------------------------------------------------------------

## 4. Relaciones y lado propietario

Las relaciones indican cómo se conectan los registros de distintas
tablas.

### `@ManyToOne`: muchos a uno

Muchos registros de una entidad pueden apuntar al mismo registro de otra
entidad.

Ejemplo: muchos Pokémon pueden pertenecer al mismo adiestrador.

Se suele poner en el lado de los muchos:

``` java
// En Pokemon
@ManyToOne
@JoinColumn(name = "adestrador")
private Adestrador adestrador;
```

La tabla `pokemon` tendrá la clave foránea `adestrador`.

### `@OneToMany`: uno a muchos

Un registro puede estar relacionado con muchos registros de otra
entidad.

Ejemplo: un adiestrador puede tener una lista de Pokémon.

``` java
// En Adestrador
@OneToMany(
    mappedBy = "adestrador",
    cascade = CascadeType.ALL,
    fetch = FetchType.EAGER
)
private List<Pokemon> pokemons;
```

Significado:

-   `mappedBy = "adestrador"`: indica que la relación está mapeada por
    el atributo Java llamado `adestrador` en la clase `Pokemon`.
-   `cascade = CascadeType.ALL`: propaga al conjunto relacionado las
    operaciones de persistencia incluidas en `ALL`. Hay que tener
    cuidado: también incluye eliminar (`REMOVE`), por lo que borrar un
    adiestrador puede borrar sus Pokémon.
-   `fetch = FetchType.EAGER`: solicita cargar la colección al cargar el
    adiestrador.
-   `fetch = FetchType.LAZY`: deja la colección sin cargar hasta que se
    acceda a ella, mientras la sesión siga disponible.

En una relación bidireccional habitual `OneToMany`/`ManyToOne`, el
`ManyToOne` es el propietario y lleva `@JoinColumn`; el `OneToMany`
lleva `mappedBy`.

### `@OneToOne`: uno a uno

Un registro de una entidad se relaciona con, como máximo, un registro de
la otra entidad, y viceversa.

Ejemplo: un adiestrador tiene un carnet.

Relación bidireccional con la FK en `Adestrador`:

``` java
// En Adestrador: lado propietario
@OneToOne
@JoinColumn(name = "carnet_id")
private Carnet carnet;

// En Carnet: lado inverso
@OneToOne(mappedBy = "carnet")
private Adestrador adestrador;
```

No es obligatorio poner la relación en las dos clases:

-   **Unidireccional:** solo una clase tiene el atributo de relación.
-   **Bidireccional:** ambas clases tienen el atributo de relación.

En una relación bidireccional, solo un lado es propietario y guarda la
clave foránea. El otro utiliza `mappedBy`.

### ¿Cómo sé cuál es el lado propietario?

El lado propietario es el que controla la relación en la base de datos.
En una relación bidireccional, normalmente es el lado que tiene
`@JoinColumn` y cuya tabla contiene la clave foránea.

El lado inverso utiliza `mappedBy`.

Ejemplo de clase:

``` java
// Pokemon: propietario
@ManyToOne
@JoinColumn(name = "adestrador")
private Adestrador adestrador;

// Adestrador: inverso
@OneToMany(mappedBy = "adestrador")
private List<Pokemon> pokemons;
```

`mappedBy = "adestrador"` apunta al **nombre del atributo Java** de
`Pokemon`, no al nombre de la columna SQL.

No es una decisión automática de Hibernate sobre qué tabla debe contener
la FK: en relaciones como `OneToOne`, el diseño decide en qué tabla se
coloca. En el ejemplo de clase, la FK está en `pokemon`, así que
`Pokemon` es el propietario.

### Tabla resumen

  -----------------------------------------------------------------------
  Relación                Significado             Mapeo habitual
  ----------------------- ----------------------- -----------------------
  `@ManyToOne`            Muchos pertenecen a uno En el lado propietario,
                                                  normalmente con
                                                  `@JoinColumn`

  `@OneToMany`            Uno tiene muchos        En el lado inverso,
                                                  normalmente con
                                                  `mappedBy`

  `@OneToOne`             Uno se relaciona con    Puede ser propietario o
                          uno                     inverso, según dónde
                                                  esté la FK
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 5. Los tres constructores

En los ejemplos de clase se preparan tres constructores por entidad.

### 1. Constructor vacío

``` java
public Pokedex() {
}
```

Hibernate necesita un constructor sin argumentos, al menos `public` o
`protected`, para poder crear instancias de la entidad.

### 2. Constructor sin ID

``` java
public Pokedex(String nome, Double peso, String misc) {
    this.nome = nome;
    this.peso = peso;
    this.misc = misc;
}
```

Se utiliza normalmente para crear objetos nuevos. No pasamos el ID
porque lo genera la base de datos.

``` java
Pokedex p = new Pokedex("Pikachu", 6.0, "Electrico");
```

### 3. Constructor con ID

``` java
public Pokedex(Long id, String nome, Double peso, String misc) {
    this.id = id;
    this.nome = nome;
    this.peso = peso;
    this.misc = misc;
}
```

Permite construir un objeto indicando también su ID. Puede resultar útil
para representar datos existentes, aunque crear un objeto con ID no
significa por sí solo que Hibernate lo haya guardado o que esté
gestionado por una sesión.

**Para recordar:** Hibernate necesita el constructor vacío. Los
constructores con y sin ID son la convención de los ejercicios de clase,
no una obligación de Hibernate.

------------------------------------------------------------------------

## 6. Getters, setters y sincronización de relaciones

Esta parte es especialmente importante en una relación bidireccional.

### Ejemplo de clase: `Adestrador` y `Pokemon`

En `Adestrador` tenemos una lista:

``` java
@OneToMany(mappedBy = "adestrador")
private List<Pokemon> pokemons;
```

En `Pokemon` tenemos la referencia al adiestrador:

``` java
@ManyToOne
@JoinColumn(name = "adestrador", nullable = true)
private Adestrador adestrador;
```

Son dos lados de la misma relación:

-   `Adestrador.pokemons`: lista de Pokémon del adiestrador.
-   `Pokemon.adestrador`: adiestrador al que pertenece ese Pokémon.

### ¿Por qué el setter de la lista tiene un `if` y un `for`?

En el ejemplo de clase:

``` java
public void setPokemons(List<Pokemon> pokemons) {
    this.pokemons = pokemons;

    if (pokemons != null) {
        for (Pokemon pokemon : pokemons) {
            pokemon.setAdestrador(this);
        }
    }
}
```

Paso a paso:

1.  `this.pokemons = pokemons;` guarda la lista recibida en el atributo
    del adiestrador.
2.  `if (pokemons != null)` comprueba que la lista no sea `null` antes
    de recorrerla. Si fuera `null`, intentar recorrerla produciría un
    `NullPointerException`.
3.  `for (Pokemon pokemon : pokemons)` recorre todos los Pokémon de la
    lista.
4.  `pokemon.setAdestrador(this)` asigna a cada Pokémon el adiestrador
    actual.

**¿Por qué hace falta?**

Porque la relación es bidireccional y hay que mantener sincronizados los
dos lados en memoria.

Si solo hacemos:

``` java
adestrador.setPokemons(listaPokemons);
```

pero no asignamos el adiestrador a cada Pokémon, podríamos tener la
lista en `Adestrador` pero que `pokemon.getAdestrador()` siguiera
devolviendo `null`.

El `if` evita recorrer una lista nula; el `for` sincroniza el lado
`ManyToOne` de cada Pokémon.

### ¿Hay que modificar también el setter del `ManyToOne`?

En el ejemplo de clase, el setter de `Pokemon` es sencillo:

``` java
public void setAdestrador(Adestrador adestrador) {
    this.adestrador = adestrador;
}
```

Esto funciona cuando llamamos a `setAdestrador()` desde el
`setPokemons()` del adiestrador, porque la lista ya se ha asignado.

Pero si cambiamos el adiestrador directamente desde un Pokémon:

``` java
pokemon.setAdestrador(nuevoAdestrador);
```

el setter sencillo cambia la referencia del Pokémon, pero no actualiza
automáticamente las listas de los dos adiestradores.

Si queremos que **cualquier cambio desde cualquiera de los dos lados**
mantenga sincronizadas ambas partes, podemos hacer setters auxiliares
más completos. Por ejemplo:

``` java
// En Adestrador
public void addPokemon(Pokemon pokemon) {
    if (pokemon == null) {
        return;
    }

    if (pokemons == null) {
        pokemons = new ArrayList<>();
    }

    if (!pokemons.contains(pokemon)) {
        pokemons.add(pokemon);
    }

    if (pokemon.getAdestrador() != this) {
        pokemon.setAdestrador(this);
    }
}
```

``` java
// En Pokemon
public void setAdestrador(Adestrador nuevoAdestrador) {

    if (this.adestrador == nuevoAdestrador) {
        return;
    }

    Adestrador anterior = this.adestrador;
    this.adestrador = nuevoAdestrador;

    if (anterior != null && anterior.getPokemons() != null) {
        anterior.getPokemons().remove(this);
    }

    if (nuevoAdestrador != null) {
        nuevoAdestrador.addPokemon(this);
    }
}
```

Estos métodos se llaman entre sí, pero las comprobaciones evitan que
entren en una llamada recursiva infinita.

Para un ejercicio sencillo como el de clase, podéis mantener el setter
de `Pokemon` simple y utilizar el `setPokemons()` con el `if` y el
`for`. Si el ejercicio requiere cambiar la relación desde ambos lados,
conviene utilizar métodos auxiliares como los anteriores.

**Importante:** sincronizar los dos lados en Java no cambia quién es el
propietario de Hibernate. En este ejemplo `Pokemon` sigue siendo el
propietario porque tiene `@JoinColumn`.

------------------------------------------------------------------------

## 7. CRUD con Hibernate

CRUD significa:

-   **Create:** crear o insertar.
-   **Read:** leer o consultar.
-   **Update:** modificar.
-   **Delete:** eliminar.

Los métodos se suelen escribir en una clase Service, por ejemplo
`PokedexService`.

### Estructura general de un Service

``` java
public class PokedexService {

    // Métodos CRUD

}
```

Para las operaciones que modifican la base de datos, el patrón de clase
es:

``` java
try (Session session =
        HibernateConfig.getSessionFactory().openSession()) {

    Transaction transaction = session.beginTransaction();

    // Operación

    transaction.commit();

} catch (Exception e) {
    System.out.println("Erro: " + e.getMessage());
}
```

-   `openSession()`: abre una sesión de Hibernate.
-   `beginTransaction()`: inicia una transacción.
-   `commit()`: confirma los cambios.
-   `rollback()`: deshace la transacción si falla.
-   El `try-with-resources` cierra la sesión automáticamente.

Las consultas de solo lectura normalmente no necesitan una transacción
explícita.

### CREATE: guardar un objeto

``` java
public void crearPokedex(Pokedex p) {

    Transaction transaction = null;

    try (Session session =
            HibernateConfig.getSessionFactory().openSession()) {

        transaction = session.beginTransaction();

        session.save(p);

        transaction.commit();

    } catch (Exception e) {

        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }

        System.out.println("Erro ao crear a Pokedex: " + e.getMessage());
    }
}
```

Llamada desde el `main`:

``` java
Pokedex p = new Pokedex("Pikachu", 6.0, "Electrico");

PokedexService service = new PokedexService();
service.crearPokedex(p);
```

`session.save(p)` guarda el objeto nuevo.

### READ: buscar por ID

``` java
public Pokedex lerPokedex(Long id) {

    try (Session session =
            HibernateConfig.getSessionFactory().openSession()) {

        return session.get(Pokedex.class, id);

    } catch (Exception e) {
        System.out.println("Erro ao ler: " + e.getMessage());
        return null;
    }
}
```

`session.get(Pokedex.class, id)` busca una entidad `Pokedex` con ese ID.
Devuelve el objeto o `null` si no existe.

Llamada:

``` java
Pokedex p = service.lerPokedex(1L);

if (p != null) {
    System.out.println(p.getNome());
}
```

### READ: buscar por nombre con HQL

``` java
public Pokedex lerPokedexPorNome(String nome) {

    try (Session session =
            HibernateConfig.getSessionFactory().openSession()) {

        return session.createQuery(
                "FROM Pokedex WHERE nome = :nome",
                Pokedex.class
        )
        .setParameter("nome", nome)
        .uniqueResult();

    } catch (Exception e) {
        System.out.println("Erro ao buscar: " + e.getMessage());
        return null;
    }
}
```

-   `createQuery()` ejecuta HQL.
-   `FROM Pokedex` utiliza el nombre de la entidad, no el nombre de la
    tabla SQL.
-   `:nome` es un parámetro.
-   `setParameter("nome", nome)` asigna el valor al parámetro.
-   `uniqueResult()` espera un único resultado o ninguno. Si hay varios
    resultados, puede producir una excepción.

### READ: listar todos

``` java
public List<Pokedex> listarPokedex() {

    try (Session session =
            HibernateConfig.getSessionFactory().openSession()) {

        return session.createQuery(
                "FROM Pokedex", Pokedex.class
        ).list();

    } catch (Exception e) {
        System.out.println("Erro ao listar: " + e.getMessage());
        return null;
    }
}
```

Devuelve una lista de objetos `Pokedex`.

### UPDATE: modificar un objeto existente

El estilo de clase consiste en buscar el registro por ID, cambiar sus
atributos y actualizarlo:

``` java
public void actualizarPokedex(Long id, Pokedex datosUpdate) {

    Transaction transaction = null;

    try (Session session =
            HibernateConfig.getSessionFactory().openSession()) {

        transaction = session.beginTransaction();

        Pokedex pokedex = session.get(Pokedex.class, id);

        if (pokedex != null) {

            pokedex.setNome(datosUpdate.getNome());
            pokedex.setPeso(datosUpdate.getPeso());
            pokedex.setMisc(datosUpdate.getMisc());

            session.update(pokedex);

            transaction.commit();

        } else {
            System.out.println("Non se atopou a Pokedex co id " + id);
            transaction.rollback();
        }

    } catch (Exception e) {

        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }

        System.out.println("Erro ao actualizar: " + e.getMessage());
    }
}
```

Llamada:

``` java
Pokedex datosNovos =
    new Pokedex("Raichu", 30.0, "Electrico evolucionado");

service.actualizarPokedex(1L, datosNovos);
```

El ID que se pasa al método indica qué registro se modifica. Los datos
del objeto `datosNovos` proporcionan los nuevos valores. No se sustituye
el ID del registro original.

**Nota sobre `session.update()`:** en este ejemplo `pokedex` se obtiene
con `session.get()` dentro de la misma sesión, por lo que ya está
gestionado por Hibernate. En ese caso, Hibernate detecta los cambios de
sus atributos automáticamente al hacer `commit()` (Dirty Checking), y
`session.update(pokedex)` no es necesario. Se muestra porque forma parte
de la sintaxis clásica de los ejemplos de clase.

### DELETE: eliminar por ID

``` java
public void eliminarPokedex(Long id) {

    Transaction transaction = null;

    try (Session session =
            HibernateConfig.getSessionFactory().openSession()) {

        transaction = session.beginTransaction();

        Pokedex pokedex = session.get(Pokedex.class, id);

        if (pokedex != null) {

            session.delete(pokedex);

            transaction.commit();

        } else {
            System.out.println("Non se atopou a Pokedex co id " + id);
            transaction.rollback();
        }

    } catch (Exception e) {

        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }

        System.out.println("Erro ao eliminar: " + e.getMessage());
    }
}
```

`session.delete(pokedex)` marca el objeto para eliminarlo de la base de
datos.

------------------------------------------------------------------------

## 8. CRUD con relaciones: ejemplo Pokemon

La entidad `Pokemon` tiene dos relaciones `ManyToOne`:

``` java
@ManyToOne
@JoinColumn(name = "adestrador", nullable = true)
private Adestrador adestrador;

@ManyToOne
@JoinColumn(name = "pokedexentry")
private Pokedex pokedex;
```

Para crear un Pokémon podemos recibir el objeto nuevo y los IDs de las
entidades relacionadas.

``` java
public void crearPokemon(
        Pokemon novoPokemon,
        Long idAdestrador,
        Long idPokedex) {

    Transaction transaction = null;

    try (Session session =
            HibernateConfig.getSessionFactory().openSession()) {

        transaction = session.beginTransaction();

        Adestrador adestrador =
            session.get(Adestrador.class, idAdestrador);

        Pokedex pokedex =
            session.get(Pokedex.class, idPokedex);

        if (adestrador == null || pokedex == null) {
            System.out.println(
                "Non existe o adestrador ou a entrada da Pokedex."
            );
            transaction.rollback();
            return;
        }

        novoPokemon.setAdestrador(adestrador);
        novoPokemon.setPokedex(pokedex);

        session.save(novoPokemon);

        transaction.commit();

    } catch (Exception e) {

        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }

        System.out.println("Erro ao crear o Pokemon: " + e.getMessage());
    }
}
```

Llamada desde el `main`:

``` java
Pokemon pikachu = new Pokemon("Pikachu", null, null);

pokemonService.crearPokemon(pikachu, 1L, 1L);
```

Pasos:

1.  Se crea el objeto `Pokemon`.
2.  Se recupera el `Adestrador` por su ID.
3.  Se recupera la `Pokedex` por su ID.
4.  Se comprueba que ambos existen.
5.  Se asignan los objetos relacionados al Pokémon.
6.  Se guarda el Pokémon y se confirma la transacción.

**Idea fundamental:** una propiedad de relación en Java contiene un
objeto (`Adestrador` o `Pokedex`), no un `Long` con el ID. Si el Service
recibe IDs, primero recupera los objetos con `session.get()` y después
los asigna.

------------------------------------------------------------------------

## 9. Chuleta para estudiar

### Anotaciones

  Anotación                Para qué sirve
  ------------------------ ---------------------------------------------------------
  `@Entity`                Marca una clase como entidad Hibernate.
  `@Table(name = "...")`   Indica el nombre de la tabla.
  `@Id`                    Marca la clave primaria.
  `@GeneratedValue`        Configura la generación del ID.
  `@Column`                Configura una columna normal.
  `@JoinColumn`            Configura la columna de clave foránea.
  `@ManyToOne`             Muchos registros se relacionan con uno.
  `@OneToMany`             Un registro se relaciona con muchos.
  `@OneToOne`              Un registro se relaciona con uno.
  `mappedBy`               Marca el lado inverso y apunta al atributo propietario.
  `cascade`                Propaga operaciones a entidades relacionadas.
  `fetch`                  Configura cuándo se cargan las relaciones.

### CRUD clásico

  Operación      Instrucción
  -------------- --------------------------------------------------
  CREATE         `session.save(objeto)`
  READ por ID    `session.get(Clase.class, id)`
  READ con HQL   `session.createQuery("FROM Clase", Clase.class)`
  UPDATE         `session.update(objeto)`
  DELETE         `session.delete(objeto)`

### Reglas que no hay que olvidar

1.  El constructor vacío es el que Hibernate necesita.
2.  El constructor sin ID es práctico para crear registros nuevos con ID
    autogenerado.
3.  `@Column` se usa para datos normales; `@JoinColumn` para la FK de
    una relación.
4.  El lado propietario controla la relación en la base de datos.
5.  En una relación bidireccional, el lado inverso utiliza `mappedBy`.
6.  `mappedBy` contiene el nombre del atributo Java del lado
    propietario.
7.  En el `setPokemons()` de `Adestrador`, el `if` evita recorrer una
    lista `null` y el `for` asigna el adiestrador a cada Pokémon.
8.  En `Pokemon`, el atributo `adestrador` es una referencia a un objeto
    `Adestrador`.
9.  Para guardar un objeto relacionado usando IDs, primero recupera los
    objetos relacionados con `session.get()`.
10. Las operaciones que modifican la base de datos se realizan dentro de
    una transacción y se confirman con `commit()`.
11. Comprueba que un objeto no sea `null` antes de modificarlo o
    eliminarlo.
12. `cascade = CascadeType.ALL` puede propagar también los borrados:
    úsalo con cuidado.

### Nota sobre versiones de Hibernate

Esta guía utiliza los métodos clásicos de los ejemplos de clase:
`save()`, `update()` y `delete()`. En código moderno, según la versión
de Hibernate, se suelen utilizar `persist()`, `merge()` y `remove()`.
Conviene seguir la API y la versión que esté utilizando el proyecto de
clase.
