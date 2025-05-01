package com.wfa.parser.restendpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wfa.parser.api.IParserOrchestrator;

@Component
@RestController
@RequestMapping("/api")
public class ParserRestEndpoint {

	private final IParserOrchestrator orchestrator;
	
	@Autowired
	public ParserRestEndpoint(IParserOrchestrator orchestrator) {
		this.orchestrator = orchestrator;
	}
	
	@PostMapping("/conductParsing")
	public ResponseEntity<Void> conductParsing() {
		orchestrator.conductParsing();
		return ResponseEntity.accepted().build();
	}
	
	@PostMapping("/exit")
	public ResponseEntity<String> exit() {
		orchestrator.exit();
		return ResponseEntity.ok("Shutting down parser application");
	}
}
