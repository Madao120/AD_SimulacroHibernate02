package model;

import javax.persistence.*;

@Entity
@Table(name = "ciudad")
public class Ciudad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nome", length = 255, nullable = false)
    private String nome;

    @Column(name = "poboacion", nullable = false)
    private int poboacion;

    @Column(name = "nivel_perigo", nullable = false)
    private int nivel_perigo;

    public Ciudad(int id, String nome, int poboacion, int nivel_perigo) {
        this.id = id;
        this.nome = nome;
        this.poboacion = poboacion;
        this.nivel_perigo = nivel_perigo;
    }

    public Ciudad(String nome, int poboacion, int nivel_perigo) {
        this.nome = nome;
        this.poboacion = poboacion;
        this.nivel_perigo = nivel_perigo;
    }

    public Ciudad() {
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

    public int getPoboacion() {
        return poboacion;
    }

    public void setPoboacion(int poboacion) {
        this.poboacion = poboacion;
    }

    public int getNivel_perigo() {
        return nivel_perigo;
    }

    public void setNivel_perigo(int nivel_perigo) {
        this.nivel_perigo = nivel_perigo;
    }

    @Override
    public String toString() {
        return "Ciudad{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", poboacion=" + poboacion +
                ", nivel_perigo=" + nivel_perigo +
                '}';
    }
}
