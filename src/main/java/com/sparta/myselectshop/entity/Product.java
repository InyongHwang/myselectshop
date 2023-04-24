package com.sparta.myselectshop.entity;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.naver.dto.ItemDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity // DB 테이블 역할을 합니다.
@NoArgsConstructor
public class Product extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID가 자동으로 생성 및 증가합니다.
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private int lprice;

    @Column(nullable = false)
    private int myprice;

    // jwt 사용하면서 추가된 컬럼
    @Column(nullable = false)
    private Long userId;

    // 이 ManyToMany는 단방향으로 걸려있다. Folder.java에서 확인하면 product 필드가 없다.
    // DB에 product_folder_list 라는 내가 만들지 않은 테이블이 생성되며, 컬럼으로는 product_id/folder_list_id가 있다.
    // ManyToMany를 사용하게 되면 이 관계를 풀어주기 위해 JPA가 스스로 중간 테이블을 생성한다.
    // 중간 테이블은 JPA가 만들기 때문에 개발자가 컬럼을 추가하거나 하는 등의 작업을 할 수 없고, JPA가 예상할 수 없는 쿼리를 날리는 등의
    // 문제가 발생할 수 있기 때문에 이런 문제를 완전히 해결하지 않은 이상 사용하지 않는 것이 좋다.
    // 앞선 Food, Order, Member의 관계와 같이 (https://2nyongs.tistory.com/70)
    // @ManyToMany를 @OneToMany @OneToMany @ManyToOne 으로 풀어줄 수 있다.
    @ManyToMany
    private List<Folder> folderList = new ArrayList<>();

    public Product(ProductRequestDto requestDto, Long userId) {
        // 입력값 Validation
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("회원 Id가 유효하지 않습니다.");
        }

        if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("저장할 수 있는 상품명이 없습니다.");
        }

        if (!isValidUrl(requestDto.getLink())) {
            throw new IllegalArgumentException("상품 최저가 페이지 링크가 적절한 URL 형식이 아닙니다.");
        }

        if (requestDto.getLprice() <= 0) {
            throw new IllegalArgumentException("상품 최저가가 0 이하입니다.");
        }

        this.userId = userId;
        this.title = requestDto.getTitle();
        this.image = requestDto.getImage();
        this.link = requestDto.getLink();
        this.lprice = requestDto.getLprice();
        this.myprice = 0;
    }

    boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        }
        catch (URISyntaxException exception) {
            return false;
        }
        catch (MalformedURLException exception) {
            return false;
        }
    }

    public void update(ProductMypriceRequestDto requestDto) {
        this.myprice = requestDto.getMyprice();
    }

    public void updateByItemDto(ItemDto itemDto) {
        this.lprice = itemDto.getLprice();
    }

    public void addFolder(Folder folder) {
        this.folderList.add(folder);
    }
}