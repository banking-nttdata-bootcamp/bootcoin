package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.BootCoin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.BootCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import com.nttdata.bootcamp.entity.dto.VirtualCoinDto;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/virtual-coin")
public class BootCoinController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BootCoinController.class);
	@Autowired
	private BootCoinService bootCoinService;

	//All Virtual-coin Registered
	@GetMapping("/findAllVirtualCoin")
	public Flux<BootCoin> findAllVirtualCoin() {
		Flux<BootCoin> virtualCoinFlux = bootCoinService.findAllBootCoin();
		LOGGER.info("All virtual coin Registered: " + virtualCoinFlux);
		return virtualCoinFlux;
	}

	//Virtual-coin registered  by customer

	@GetMapping("/findVirtualCoinByCellNumber/{cellNumber}")
	public Mono<BootCoin> findVirtualCoinByCellNumber(@PathVariable("cellNumber") String cellNumber) {
		Mono<BootCoin> virtualCoinMono = bootCoinService.findBootCoinByCellNumber(cellNumber);
		LOGGER.info("Virtual coin Registered by cell number "+cellNumber+": " + virtualCoinMono);
		return virtualCoinMono;
	}

	//Save Virtual-coin
	@PostMapping(value = "/saveVirtualCoin")
	public Mono<BootCoin> saveVirtualCoin(@RequestBody VirtualCoinDto virtualCoinDto){

		BootCoin bootCoin = new BootCoin();
		Mono.just(bootCoin).doOnNext(t -> {
			t.setDni(virtualCoinDto.getDni());
			t.setEmail(virtualCoinDto.getEmail());
			t.setCellNumber(virtualCoinDto.getCellNumber());
			t.setNumberAccount("");
			t.setMount(0.00);
			t.setFlagDebitCard(false);
			t.setNumberDebitCard("");
			t.setTypeOperation("REGISTER");
			t.setCreationDate(new Date());
			t.setModificationDate(new Date());
		}).onErrorReturn(bootCoin).onErrorResume(e -> Mono.just(bootCoin))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<BootCoin> passiveMono = bootCoinService.saveBootCoin(bootCoin);
		return passiveMono;
	}

}
