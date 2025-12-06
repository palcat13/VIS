package cs.vsb.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs.vsb.domain.RaceEntry;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ResultJsonExportMapper {
    private final ObjectMapper objectMapper;

    public ResultJsonExportMapper() {
        this.objectMapper = new ObjectMapper();
    }



    public String generateJsonString(List<RaceEntry> entries) {
        try {
            List<RaceResultDto> dtos = mapToDto(entries);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dtos);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to generate JSON string", e);
        }
    }

    private List<RaceResultDto> mapToDto(List<RaceEntry> entries) {
        return entries.stream()
                .map(e -> new RaceResultDto(
                        e.getRacer().getFname() + " " + e.getRacer().getLname(),
                        e.getRaceNumber(),
                        e.getRaceTime(),
                        e.getPlace(),
                        e.getRace().getName()
                ))
                .collect(Collectors.toList());
    }

    private record RaceResultDto(String racerName, int number, long timeMillis, int place, String raceName) {}
}