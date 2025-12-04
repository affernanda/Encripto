<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="JavaBeans.Endereco, JavaBeans.Usuario, JavaBeans.Carrinho, JavaBeans.CarrinhoTemporario, JavaBeans.Produto, java.sql.ResultSet, java.util.List" %>

<%
    Usuario user = (Usuario) session.getAttribute("usuarioLogado");
    CarrinhoTemporario carrinhotempDAO = new CarrinhoTemporario();
    carrinhotempDAO.session_id = (Integer) session.getAttribute("session_id_int");
    boolean deslogado = (user == null);

// LISTA ENDEREÇOS
    List<Endereco> lista = null;
    if (!deslogado) {
        Endereco enderecoDAO = new Endereco();
        lista = enderecoDAO.listarEnderecos(user.pkuser);
    }

// LISTA CARRINHO
    ResultSet itens = null;
    if (!deslogado) {
        Carrinho carrinhoDAO = new Carrinho();
        itens = carrinhoDAO.listarCarrinho(user.pkuser);
    } else {
        itens = carrinhotempDAO.listarCarrinhoTemp(carrinhotempDAO.session_id);
    }
    double totalCarrinho = 0;
%>

<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Encripto - Carrinho</title>
        <link rel="stylesheet" href="../css/carrinho.css" />
        <style>
            @font-face {
                font-family: 'FonteMarca';
                src: url('../fonts/calora.otf') format('opentype');
            }
            .cart-container {
                flex: 2;
                border: 1px solid #eee;
                border-radius: 12px;
                padding: 25px;
                background: #fff;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
                margin-top: 30px;
            }
            .cart-header, .cart-item {
                display: grid;
                grid-template-columns: 3fr 1fr 1fr 1fr;
                align-items: center;
                padding: 10px;
                border-bottom: 1px solid #ddd;
            }
            .cart-header {
                font-weight: bold;
                background-color: #eee;
                color:black;
            }
            .cart-item img {
                width: 100px;
                height: 140px;
                border-radius: 10px;
            }
            .cart-total {
                margin-top: 10px;
                text-align: right;
                font-size: 15px;
            }
            .remove-btn {
                color: red;
                font-size: 20px;
                text-decoration: none;
            }
            .summary {
                margin-top: 30px;
                padding: 20px;
                background: #f4f4f4;
                border-radius: 10px;
            }
            .enderecos-lista {
                margin-top: 15px;
                padding: 10px;
                border-radius: 10px;
                background-color: #f9f9f9;
            }
            .endereco-item {
                border-bottom: 1px solid #ddd;
                padding: 8px 0;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }
            .endereco-item:last-child {
                border-bottom: none;
            }
            .principal {
                color: green;
                font-weight: bold;
            }
            .secundario {
                color: #555;
            }
            button[type="submit"], .botao-finalizar {
                background-color: black;
                color: white;
                padding: 10px 20px;
                border-radius: 8px;
                border: none;
                cursor: pointer;
                font-size: 16px;
                text-decoration: none;
            }
            button[type="submit"]:hover, .botao-finalizar:hover {
                background-color: #ff1a45;
            }
            .frete-container {
                display: flex;
                align-items: flex-start;
                gap: 15px;
                margin-top: 15px;
                background: #fff;
                padding: 15px;
                border-radius: 10px;
                box-shadow: 0 2px 6px rgba(0,0,0,0.1);
            }

            /* Botão Calcular Frete */
            #btn-frete {
                background-color: black;
                color: white;
                padding: 10px 15px;
                border-radius: 8px;
                border: none;
                cursor: pointer;
                font-size: 17px;
                min-width: 180px;
                height: 100%;
                text-align: center;
                transition: background 0.2s ease;
            }

            #btn-frete:hover {
                background-color: #ff1a45;
            }

            .voucher {
                margin-top: 20px;
                display: flex;
                flex-direction: column;
                align-items: flex-start;
                gap: 12px;
            }

            /* Grupo das opções de frete */
            #opcoes-frete {
                flex: 1;
            }

            #opcoes-frete p {
                font-weight: bold;
                margin-bottom: 8px;
            }

            #opcoes-frete label {
                display: flex;
                align-items: center;
                width: 280px;
                justify-content: space-between;
                background: #f7f7f7;
                border: 1px solid #ddd;
                border-radius: 8px;
                padding: 8px 12px;
                margin-bottom: 6px;
                cursor: pointer;
                transition: all 0.2s ease;
            }

            #opcoes-frete label:hover {
                background-color: #eee;
            }

            /* ================= PAGAMENTO ================= */
            .payment-option {
                display: flex;
                align-items: center;
                gap: 10px;
                border: 2px solid #ddd;
                border-radius: 10px;
                padding: 12px 16px;
                margin-bottom: 10px;
                cursor: pointer;
                transition: all 0.2s ease-in-out;
                background-color: #fafafa;
            }

            .payment-option:hover {
                border-color: #000;
                background-color: #f2f2f2;
            }

            .payment-option input[type="radio"] {
                accent-color: #000;
                transform: scale(1.2);
            }

            #infoPagamento {
                margin-top: 15px;
                font-size: 14px;
            }

            #infoPagamento input,
            #infoPagamento select {
                width: 100%;
                padding: 8px;
                border: 1px solid #ccc;
                border-radius: 6px;
                outline: none;
                font-size: 14px;
            }

            #infoPagamento label {
                font-weight: 600;
                color: #333;
            }

            footer {
                padding: 80px 40px 50px;
                display: flex;
                flex-direction: column;
                align-items: center;
                text-align: center;
                margin: 0;
            }

            .footer-logo {
                font-family: 'FonteMarca', sans-serif;
                font-size: 180px;
                font-weight: 900;
                letter-spacing: -8px;
                color: #e3e3e3;
                opacity: 0.45;
                line-height: 1;
                margin: 0;
                text-align:center;
            }
        </style>
    </head>

    <body>
        <header>
            <div class="logo">
                <% if (!deslogado) { %>
                <a href="../index_logado.jsp"
                   style="text-decoration: none; color: white; font-family: 'FonteMarca'; font-size: 50px;">
                    Encripto
                </a>
                <% } else {%>
                <a href="../index.jsp"
                   style="text-decoration: none; color: white; font-family: 'FonteMarca'; font-size: 50px;">
                    Encripto
                </a>
                <% }%>
            </div>
            <nav>
                <a href="perfil.jsp" class="cart">👤</a>
            </nav>
        </header>

        <main>
            <div class="container">
                <!-- ===================== CARRINHO ===================== -->
                <div class="cart-container">
                    <h2>Resumo do Pedido</h2><br>
                    <div class="cart-header">
                        <span>Produto</span>
                        <span>Quantidade</span>
                        <span>Subtotal</span>
                        <span>Excluir</span>
                    </div>
                    <% if (!deslogado) { %>
                    <%
                        while (itens != null && itens.next()) {
                            int pkcar = itens.getInt("pkcar");
                            int pkproduto = itens.getInt("pkproduto");
                            String nome = itens.getString("nome");
                            String valorStr = itens.getString("valor");
                            double valor = Double.parseDouble(valorStr);
                            int quantidade = itens.getInt("quantidade");
                            String imagem = itens.getString("imagem");
                            double subtotal = valor * quantidade;
                            totalCarrinho += subtotal;
                    %>
                    <div class="cart-item">
                        <div style="display:flex; align-items:center; gap:10px;">
                            <img src="../<%= imagem%>" width="150" height="150" alt="<%= nome%>">
                            <span><%= nome%></span>
                        </div>
                        <span><%= quantidade%></span>
                        <span>R$ <%= String.format("%.2f", subtotal)%></span>
                        <span>
                            <a href="remover_item.jsp?pkcar=<%= pkcar%>" class="remove-btn">🗑️</a>
                        </span>
                    </div>
                    <% }%>

                    <% } else { %>
                    <%
                        while (itens != null && itens.next()) {
                            int pkcartemp = itens.getInt("pkcartemp");
                            int pkproduto = itens.getInt("pkproduto");
                            String nome = itens.getString("nome");
                            String valorStr = itens.getString("valor");
                            double valor = Double.parseDouble(valorStr);
                            int quantidade = itens.getInt("quantidade");
                            String imagem = itens.getString("imagem");
                            double subtotal = valor * quantidade;
                            totalCarrinho += subtotal;
                    %>
                    <div class="cart-item">
                        <div style="display:flex; align-items:center; gap:10px;">
                            <img src="../<%= imagem%>" width="150" height="150" alt="<%= nome%>">
                            <span><%= nome%></span>
                        </div>
                        <span><%= quantidade%></span>
                        <span>R$ <%= String.format("%.2f", subtotal)%></span>
                        <span>
                            <a href="remover_item.jsp?pkcartemp=<%= pkcartemp%>" class="remove-btn">🗑️</a>
                        </span>
                    </div>
                    <% }%>     
                    <% }%>
                    <div class="cart-total">
                        Subtotal: R$ <span id="total-carrinho"><%= String.format("%.2f", totalCarrinho)%></span><br>
                        Frete: R$ <span id="freteExibido">0,00</span><BR>
                        Total: R$ <span id="totalFinal">0,00</span>
                    </div>
                </div>
                <!-- ===================== RESUMO / ENDEREÇOS ===================== -->
                <% if (!deslogado) { %>
                <div class="summary">
                    <div class="address-box">
                        <div>
                            <form action="resumo_pedido.jsp" method="post">
                                <h3>Forma de Pagamento</h3>
                                <div class="payment-option">
                                    <input type="radio" name="pagamento" id="boleto" value="Boleto bancario" required>
                                    <label for="boleto">💵 Boleto Bancário</label>
                                </div>
                                <div class="payment-option">
                                    <input type="radio" name="pagamento" id="cartao" value="Cartao de credito" required>
                                    <label for="cartao">💳 Cartão de Crédito</label>
                                </div>
                                <div id="infoPagamento" style="margin-top:20px;"></div>
                                <h3>Endereço de Entrega</h3>
                                <p>Selecione um endereço cadastrado:</p>
                                <div>
                                    <%
                                        if (lista != null && !lista.isEmpty()) {
                                            for (Endereco e : lista) {
                                    %>
                                    <div class="payment-option">
                                        <label>
                                            <input type="radio" name="enderecoSelecionado" value="<%= e.pkenderecos%>"  required>
                                            <%= e.logradouro%>, nº <%= e.numero%>
                                            <% if (e.complemento != null && !e.complemento.isEmpty()) {%>
                                            - <%= e.complemento%>
                                            <% }%>
                                            <span class="<%= e.padrao ? "principal" : "secundario"%>">
                                                (<%= e.padrao ? "Principal" : "Secundário"%>)
                                            </span>
                                        </label>
                                    </div>
                                    <% }
                                    } else { %>
                                    <p>Nenhum endereço cadastrado.</p>
                                    <% }%>
                                </div>
                                <div style="margin-top: 20px; margin-bottom: 30px;">
                                    <a href="../enderecos_editar.html?origem=carrinho" class="botao-finalizar">Adicionar Endereço</a>
                                </div>

                                <h3>Entrega e Frete</h3>
                                <div class="payment-option">
                                    <input type="radio" name="frete" id="economico" value="5">
                                    <label for="economico"> ⏱️ Econômico | 10 dias (R$5,00)</label>
                                </div>
                                <div class="payment-option">
                                    <input type="radio" name="frete" id="normal" value="10">
                                    <label for="normal"> 📦 Padrão | 5 dias (R$10,00)</label>
                                </div>
                                <div class="payment-option">
                                    <input type="radio" name="frete" id="expresso" value="20">
                                    <label for="expresso"> ⚡ Expresso | 2 dias (R$20,00)</label>
                                </div>
                                <!-- ========== FORMULÁRIO FINAL ========== -->

                                <input type="hidden" name="totalCompra" id="input-total" value="<%= totalCarrinho%>">
                                <input type="hidden" name="frete" id="input-frete" value="0">
                                <input type="hidden" name="pagamento" id="input-pagamento">
                                <button type="submit" class="botao-finalizar">Próxima Etapa</button>
                            </form>
                        </div>
                    </div>
                </div>
                <% } else {%>
                <div class="summary">
                    <div class="address-box">
                        <div>
                            <form action="../login.html" method="post">
                                <h3>Entrega e Frete</h3>
                                <div class="payment-option">
                                    <input type="radio" name="frete" id="economico" value="5.00" required>
                                    <label for="economico"> ⏱️ Econômico | 10 dias (R$5,00)</label>
                                </div>
                                <div class="payment-option">
                                    <input type="radio" name="frete" id="normal" value="10.00" required>
                                    <label for="normal"> 📦 Padrão | 5 dias (R$10,00)</label>
                                </div>
                                <div class="payment-option">
                                    <input type="radio" name="frete" id="expresso" value="20.00" required>
                                    <label for="expresso"> ⚡ Expresso | 2 dias (R$20,00)</label>
                                </div>
                                <!-- ========== FORMULÁRIO FINAL ========== -->
                                <input type="hidden" name="totalCompra" id="input-total" value="<%= totalCarrinho%>">
                                <input type="hidden" name="freteSelecionado" id="input-frete" value="0">
                                <button type="submit" class="botao-finalizar">Próxima Etapa</button>
                            </form>
                        </div>
                    </div>
                </div>
                <% }%>
            </div>
        </main>

        <footer>
            <div class="footer-logo">ENCRIPTO</div>
        </footer>

        <script>
            document.querySelectorAll('input[name="pagamento"]').forEach((input) => {
                input.addEventListener('change', function () {
                    const container = document.getElementById("infoPagamento");

                    if (this.value === "Boleto bancario") {
                        container.innerHTML = `
                <p>Você selecionou <strong>Boleto Bancário</strong>.</p>
                <p>O boleto será enviado por e-mail após a confirmação do pedido.</p>`;
                    }

                    if (this.value === "Cartao de credito") {
                        container.innerHTML = `
                <div style="display:flex; flex-direction:column; gap:10px;">
                    <label>Número do Cartão:
                        <input type="text" placeholder="0000 0000 0000 0000" required>
                    </label>
                    <label>Nome Completo:
                        <input type="text" placeholder="Nome impresso no cartão" required>
                    </label>
                    <label>Código de Segurança (CVV):
                        <input type="text" placeholder="123" required>
                    </label>
                    <label>Validade:
                        <input type="month" required>
                    </label>
                    <label>Parcelas:
                        <select>
                            <option>1x sem juros</option>
                            <option>2x sem juros</option>
                            <option>3x sem juros</option>
                            <option>4x sem juros</option>
                            <option>5x sem juros</option>
                            <option>6x sem juros</option>
                        </select>
                    </label>
                </div><br>
            `;
                    }
                });
            });

    const freteRadios = document.querySelectorAll('input[name="frete"]');
    const inputFrete = document.getElementById('input-frete');
    const freteExibido = document.getElementById('freteExibido'); 
    const totalCarrinho = parseFloat(document.getElementById('total-carrinho').textContent.replace(",", "."));
    const totalFinalSpan = document.getElementById('totalFinal');

    freteRadios.forEach(radio => {
        radio.addEventListener('change', () => {

            const frete = parseFloat(radio.value);  // valor numérico do frete

            // Atualiza o hidden
            inputFrete.value = frete;

            // Mostra o frete no HTML
            freteExibido.textContent = frete.toFixed(2).replace('.', ',');

            // Calcula o total final
            const totalFinal = totalCarrinho + frete;

            // Mostra no HTML
            totalFinalSpan.textContent = totalFinal.toFixed(2).replace('.', ',');
        });
    });


        </script>
    </body>
</html>
