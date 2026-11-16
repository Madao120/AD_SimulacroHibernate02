package model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (name = "equipo")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column (name = "nome", nullable = false, length = 255)
    private String nome;

    @Column (name = "base", length = 255, nullable = false)
    private String base;

    @OneToMany (mappedBy = "equipo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Superheroe> superheroes;

    public Equipo(int id, String nome, String base, List<Superheroe> superheroes) {
        this.id = id;
        this.nome = nome;
        this.base = base;
        this.superheroes = superheroes;
    }

    public Equipo(String nome, String base, List<Superheroe> superheroes) {
        this.nome = nome;
        this.base = base;
        this.superheroes = superheroes;
    }

    public  Equipo(){}

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

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public List<Superheroe> getSuperheroes() {
        return superheroes;
    }

    public void setSuperheroes(List<Superheroe> superheroes) {
        this.superheroes = superheroes;

        if (superheroes != null){
            for (Superheroe superheroe : superheroes){
                superheroe.setEquipo(this);
            }
        }
    }

    @Override
    public String toString() {
        return "Equipo{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", base='" + base + '\'' +
                ", superheroes=" + superheroes +
                '}';
    }
}
