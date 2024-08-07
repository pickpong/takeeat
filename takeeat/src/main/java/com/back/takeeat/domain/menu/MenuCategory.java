package com.back.takeeat.domain.menu;

import com.back.takeeat.domain.market.Market;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MenuCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_category_id")
    private Long id;

    private String menuCategoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id")
    private Market market;

    @OneToMany(mappedBy = "menuCategory", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Menu> menus;

    public void addMenus(List<Menu> menus) {
        this.menus = menus;
    }
}
