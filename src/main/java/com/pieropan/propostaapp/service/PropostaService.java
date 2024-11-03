package com.pieropan.propostaapp.service;

import com.pieropan.propostaapp.dto.PropostaRequestDto;
import com.pieropan.propostaapp.dto.PropostaResponseDto;
import com.pieropan.propostaapp.entity.Proposta;
import com.pieropan.propostaapp.mapper.PropostaMapper;
import com.pieropan.propostaapp.repository.PropostaRepository;
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

        notificarRabbitMQ(proposta);

        return PropostaMapper.INSTANCE.convertEntitytoDto(proposta);
    }

    private void notificarRabbitMQ(Proposta proposta){
       try {
           notificacaoService.notificar(proposta, exchange);
       }catch (RuntimeException e){
           proposta.setIntegrada(false);
           propostaRepository.save(proposta);
       }
    }

    public List<PropostaResponseDto> obterProposta() {
        propostaRepository.findAll();
        return null;
    }
}
