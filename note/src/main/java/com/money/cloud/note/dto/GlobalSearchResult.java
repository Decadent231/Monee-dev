package com.money.cloud.note.dto;

import lombok.Data;

import java.util.List;

@Data
public class GlobalSearchResult {

    private List<Item> notes;
    private List<Item> vaultItems;
    private List<Item> todos;

    @Data
    public static class Item {
        private Long id;
        private String title;
        private String subtitle;
        private String module;

        public Item(Long id, String title, String subtitle, String module) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
            this.module = module;
        }
    }
}
