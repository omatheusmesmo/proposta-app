package com.pieropan.propostaapp.service;

import com.pieropan.propostaapp.dto.PropostaRequestDto;
import com.pieropan.propostaapp.dto.PropostaResponseDto;
import com.pieropan.propostaapp.entity.Proposta;
import com.pieropan.propostaapp.mapper.PropostaMapper;
import com.pieropan.propostaapp.repository.PropostaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropostaService {


    private String exchange;

    private PropostaRepository propostaRepository;

    private NotificacaoService notificacaoService;

    public PropostaService(@Value("${rabbitmq.propostapendente.exchange}") String exchange, PropostaRepository propostaRepository, NotificacaoService notificacaoService) {
        this.exchange = exchange;
        this.propostaRepository = propostaRepository;
        this.notificacaoService = notificacaoService;
    }

    public PropostaResponseDto criar (PropostaRequestDto requestDto){
        Proposta proposta = PropostaMapper.INSTANCE.convertDtoToProposta(requestDto);
        propostaRepository.save(proposta);

        PropostaResponseDto response = PropostaMapper.INSTANCE.convertEntitytoDto(proposta);
        notificacaoService.notificar(response, exchange);

        return response;
    }

    public List<PropostaResponseDto> obterProposta() {
        propostaRepository.findAll();
        return null;
    }
}
