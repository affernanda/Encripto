document.addEventListener("DOMContentLoaded", function () {
  const cepInput = document.getElementById("cep");

  cepInput.addEventListener("input", function () {
    let valor = this.value.replace(/\D/g, "");
    if (valor.length > 8) valor = valor.slice(0, 8);
    if (valor.length > 5) {
      this.value = valor.slice(0, 5) + "-" + valor.slice(5);
    } else {
      this.value = valor;
    }
  });

  cepInput.addEventListener("blur", async function () {
    let cep = this.value.replace(/\D/g, "");
    limparCamposEndereco();

    if (cep.length !== 8) {
      if (cep.length > 0) alert("CEP inválido! Digite 8 números.");
      return;
    }

    try {
      const resposta = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
      const dados = await resposta.json();

      if (dados.erro) {
        alert("CEP não encontrado!");
        return;
      }

      // Preenche automaticamente os campos
      document.getElementById("logradouro").value = dados.logradouro || "";
      document.getElementById("bairro").value = dados.bairro || "";
      document.getElementById("cidade").value = dados.localidade || "";
      document.getElementById("uf").value = dados.uf || "";

    } catch (erro) {
      alert("Erro ao buscar o CEP: " + erro.message);
    }
  });

  function limparCamposEndereco() {
    document.getElementById("logradouro").value = "";
    document.getElementById("bairro").value = "";
    document.getElementById("cidade").value = "";
    document.getElementById("uf").value = "";
  }
});
