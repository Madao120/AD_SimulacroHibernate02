package model;

import javax.persistence.*;

@Entity
@Table(name = "superheroe")
public class Superheroe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nome", length = 255, nullable = false)
    private String nome;

    @Column(name = "nome_real", length = 255, nullable = false)
    private String nome_real;

    @Column(name = "poder", length = 255, nullable = false)
    private String poder;

    @OneToOne
    @JoinColumn(name = "ciudad_id", nullable = false)
    private Ciudad ciudad;

    @ManyToOne
    @JoinColumn(name= "equipo_id", nullable = false)
    private Equipo equipo;

    public Superheroe(int id, String nome, String nome_real, String poder, Ciudad ciudad, Equipo equipo) {
        this.id = id;
        this.nome = nome;
        this.nome_real = nome_real;
        this.poder = poder;
        this.ciudad = ciudad;
        this.equipo = equipo;
    }

    public Superheroe(String nome, String nome_real, String poder, Ciudad ciudad, Equipo equipo) {
        this.nome = nome;
        this.nome_real = nome_real;
        this.poder = poder;
        this.ciudad = ciudad;
        this.equipo = equipo;
    }

    public Superheroe() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome_real() {
        return nome_real;
    }

    public void setNome_real(String nome_real) {
        this.nome_real = nome_real;
    }

    public String getPoder() {
        return poder;
    }

    public void setPoder(String poder) {
        this.poder = poder;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    @Override
    public String toString() {
        return "Superheroe{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", nome_real='" + nome_real + '\'' +
                ", poder='" + poder + '\'' +
                ", ciudad=" + ciudad +
                ", equipo=" + equipo.getNome() +
                '}';
    }
}
