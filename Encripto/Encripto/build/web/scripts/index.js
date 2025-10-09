// ================== CARROSSEL ==================
const carousel = document.getElementById("carousel");
let scrollAmount = 0;

function autoScroll() {
  if (carousel.scrollWidth - carousel.clientWidth <= scrollAmount) {
    scrollAmount = 0;
  } else {
    scrollAmount += 2;
  }
  carousel.scrollTo({ left: scrollAmount, behavior: "smooth" });
}
setInterval(autoScroll, 80);

// ================== CARRINHO LATERAL ==================
const cartBtn = document.getElementById("cart-btn");
const cartSidebar = document.getElementById("cart-sidebar");
const closeCart = document.getElementById("close-cart");

cartBtn.addEventListener("click", (e) => {
  e.preventDefault();
  cartSidebar.classList.add("active");
});
closeCart.addEventListener("click", () => {
  cartSidebar.classList.remove("active");
});
window.addEventListener("click", (e) => {
  if (e.target === cartSidebar) cartSidebar.classList.remove("active");
});

// ================== CLASSES ==================
class Product {
  constructor(id, nome, preco, img) {
    this.id = id;
    this.nome = nome;
    this.preco = preco;
    this.img = img;
  }
}

class CartItem {
  constructor(product, qtd = 1) {
    this.product = product;
    this.qtd = qtd;
  }
  get subtotal() {
    return this.product.preco * this.qtd;
  }
}

class Storage {
  static saveCart(items) {
    localStorage.setItem("cart", JSON.stringify(items));
  }
  static getCart() {
    const data = localStorage.getItem("cart");
    return data ? JSON.parse(data) : [];
  }
}

// ================== CLASSE PRINCIPAL DO CARRINHO ==================
class Cart {
  constructor() {
    const saved = Storage.getCart();
    this.items = saved.map(
      (i) =>
        new CartItem(
          new Product(i.product.id, i.product.nome, i.product.preco, i.product.img),
          i.qtd
        )
    );
    this.frete = 0;
  }

  add(prod) {
    const existing = this.items.find((i) => i.product.id === prod.id);
    if (existing) existing.qtd++;
    else this.items.push(new CartItem(prod));
    this.update();
  }

  increase(id) {
    const item = this.items.find((i) => i.product.id === id);
    if (item) item.qtd++;
    this.update();
  }

  decrease(id) {
    const item = this.items.find((i) => i.product.id === id);
    if (!item) return;
    item.qtd--;
    if (item.qtd <= 0) this.remove(id);
    this.update();
  }

  remove(id) {
    this.items = this.items.filter((i) => i.product.id !== id);
    this.update();
  }

  get subtotal() {
    return this.items.reduce((acc, i) => acc + i.subtotal, 0);
  }

  get total() {
    return this.subtotal + this.frete;
  }

  update() {
    Storage.saveCart(this.items);
    renderCart();
  }
}

// ================== INSTÂNCIA DO CARRINHO ==================
const cart = new Cart();

// ================== FUNÇÃO PARA RENDERIZAR ==================
function renderCart() {
  const container = document.getElementById("cart-items");
  const subtotal = document.getElementById("cart-subtotal");
  const frete = document.getElementById("cart-frete");
  const total = document.getElementById("cart-total");

  if (!container || !subtotal || !frete || !total) return;

  if (cart.items.length === 0) {
    container.innerHTML = "<p>Nenhum item no carrinho.</p>";
  } else {
    container.innerHTML = cart.items
      .map(
        (i) => `
      <div class="cart-item">
        <img src="${i.product.img}" width="60" alt="${i.product.nome}">
        <div class="info">
          <strong>${i.product.nome}</strong><br>
          <span>R$ ${i.product.preco.toFixed(2)}</span><br>
          <div class="qty">
            <button onclick="cart.decrease(${i.product.id})">-</button>
            <span>${i.qtd}</span>
            <button onclick="cart.increase(${i.product.id})">+</button>
            <button onclick="cart.remove(${i.product.id})">🗑</button>
          </div>
        </div>
      </div>`
      )
      .join("");
  }

  subtotal.textContent = `R$ ${cart.subtotal.toFixed(2)}`;
  frete.textContent = `R$ ${cart.frete.toFixed(2)}`;
  total.textContent = `R$ ${cart.total.toFixed(2)}`;
}
// ================== DETALHES DO PRODUTO ==================

const detalheBtns = document.querySelectorAll(".cards .btn");
detalheBtns.forEach((btn, index) => {
  btn.addEventListener("click", (e) => {
    const card = btn.closest(".card");
    const nome = card.querySelector("h3").textContent.trim();
    const precoTexto = card.querySelector("p").textContent.replace("R$", "").replace(",", ".").trim();
    const preco = parseFloat(precoTexto);
    const img = card.querySelector("img").getAttribute("src");

    // Salva os dados do produto no localStorage
    const produtoSelecionado = { nome, preco, img };
    localStorage.setItem("produtoSelecionado", JSON.stringify(produtoSelecionado));
  });
});

// ================== PRODUTOS ==================
// Identifica todos os cards da seção "Mais Vendidos"
const cards = document.querySelectorAll(".cards .card");
cards.forEach((card, index) => {
  const nome = card.querySelector("h3").textContent.trim();
  const precoTexto = card.querySelector("p").textContent.replace("R$", "").replace(",", ".").trim();
  const preco = parseFloat(precoTexto);
  const img = card.querySelector("img").getAttribute("src");

  // Cria um produto com ID incremental
  const produto = new Product(index + 1, nome, preco, img);

  // Cria um botão de adicionar
  const btnAdd = document.createElement("button");
  btnAdd.textContent = "Adicionar ao carrinho";
  btnAdd.classList.add("add-btn");
  btnAdd.addEventListener("click", (e) => {
    e.preventDefault();
    cart.add(produto);
    cartSidebar.classList.add("active");
  });

  // Adiciona o botão ao card (dentro de .card-info)
  card.querySelector(".card-info").appendChild(btnAdd);
});

// ================== FRETE ==================
const cepInput = document.getElementById("cep-input");
const btnFrete = document.getElementById("btn-calcular-frete");
const opcoesFrete = document.getElementById("opcoes-frete");

if (btnFrete) {
  btnFrete.addEventListener("click", () => {
    const cep = cepInput.value.trim();
    if (!cep) return alert("Digite um CEP válido!");

    const opcoes = [
      { tipo: "Econômico", valor: 10 },
      { tipo: "Normal", valor: 20 },
      { tipo: "Expresso", valor: 35 },
    ];

    opcoesFrete.innerHTML = opcoes
      .map(
        (o) => `
        <label>
          <input type="radio" name="frete" value="${o.valor}">
          ${o.tipo} - R$ ${o.valor.toFixed(2)}
        </label><br>`
      )
      .join("");

    document.querySelectorAll("input[name='frete']").forEach((el) => {
      el.addEventListener("change", (e) => {
        cart.frete = parseFloat(e.target.value);
        renderCart();
      });
    });
  });
}

// ================== INICIALIZAÇÃO ==================
renderCart();
