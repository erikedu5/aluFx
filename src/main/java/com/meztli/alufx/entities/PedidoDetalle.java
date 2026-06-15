package com.meztli.alufx.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "pedido_detalles")
public class PedidoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @Column(name = "tipoCorte")
    private String tipoCorte;

    @Column(name = "anchoResultado")
    private String anchoResultado;

    @Column(name = "altoResultado")
    private String altoResultado;

    public PedidoDetalle() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getTipoCorte() {
        return tipoCorte;
    }

    public void setTipoCorte(String tipoCorte) {
        this.tipoCorte = tipoCorte;
    }

    public String getAnchoResultado() {
        return anchoResultado;
    }

    public void setAnchoResultado(String anchoResultado) {
        this.anchoResultado = anchoResultado;
    }

    public String getAltoResultado() {
        return altoResultado;
    }

    public void setAltoResultado(String altoResultado) {
        this.altoResultado = altoResultado;
    }
}
