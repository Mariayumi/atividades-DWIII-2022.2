package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelos.EnderecoAtualizador;
import com.autobots.automanager.modelos.EnderecoSelecionador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@RestController
public class EnderecoControle {
	
	@Autowired
	private EnderecoSelecionador selecionador;
	@Autowired
	private ClienteRepositorio repositorioCliente;
	@Autowired
	private EnderecoRepositorio repositorio;

	@GetMapping("/enderecos/{endeId}")
	public ResponseEntity<Endereco> obterEndereco(@PathVariable Long endeId){
		List<Endereco> enderecos = repositorio.findAll();
		Endereco endereco = selecionador.selecionar(enderecos, endeId);
		if(endereco == null) {
			ResponseEntity<Endereco> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			ResponseEntity<Endereco> resposta = new ResponseEntity<Endereco> (endereco, HttpStatus.FOUND);
			return resposta;
		}
	}
	
	@GetMapping("/enderecos")
	public ResponseEntity<List<Endereco>> obterEnderecos(){
		List<Endereco> enderecos = repositorio.findAll();
		if (enderecos.isEmpty()) {
			ResponseEntity<List<Endereco>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			ResponseEntity<List<Endereco>> resposta = new ResponseEntity<>(enderecos, HttpStatus.FOUND);
			return resposta;
		}
	}
	
	@PutMapping("enderecos/cadastro/{clienteId}")
	public ResponseEntity<?> cadastroEndereco(@PathVariable Long clienteId, @RequestBody Endereco atualizacao){
		HttpStatus status = HttpStatus.CONFLICT;
		Cliente cliente = repositorioCliente.getById(clienteId);
		if (cliente != null) {
			cliente.setEndereco(atualizacao);
			repositorio.save(atualizacao);
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(status);
	}
	
	@PutMapping("/enderecos/atualizacao/{endeId}")
	public ResponseEntity<?> atualizarEndereco(@PathVariable Long endeId, @RequestBody Endereco atualizacao){
		HttpStatus status = HttpStatus.CONFLICT;
		Endereco endereco = repositorio.getById(endeId);
		if (endereco != null) {
			EnderecoAtualizador atualizador = new EnderecoAtualizador();
			atualizador.atualizar(endereco, atualizacao);
			repositorio.save(endereco);
			status = HttpStatus.CREATED;
		}else {
			status = HttpStatus.BAD_REQUEST;
		} return new ResponseEntity<>(status);
	}

	@DeleteMapping("/enderecos/deletar/{clienteId}/{endeId}")
	public ResponseEntity<?> excluirEndereco(@PathVariable Long endeId, @PathVariable Long clienteId){
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Endereco endereco = repositorio.getById(endeId);
		Cliente cliente = repositorioCliente.getById(clienteId);
		if (endereco != null) {
			cliente.getEndereco().remove(endereco);
			repositorio.delete(endereco);
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(status);
	}
}
