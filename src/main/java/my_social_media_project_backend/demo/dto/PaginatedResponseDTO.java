package my_social_media_project_backend.demo.dto;

import java.util.List;

public class PaginatedResponseDTO<T> {

    private List<T> data;
    private long total;
    private int start;
    private int length;

    public PaginatedResponseDTO(
            List<T> data,
            long total,
            int start,
            int length
    ) {
        this.data = data;
        this.total = total;
        this.start = start;
        this.length = length;
    }

    public List<T> getData() {
        return data;
    }

    public long getTotal() {
        return total;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }
}