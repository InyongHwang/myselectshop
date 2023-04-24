package com.sparta.myselectshop.service;

import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.repository.ProductRepository;
import com.sparta.myselectshop.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 로그인한 회원에 폴더를 등록
    @Transactional
    public List<Folder> addFolders(List<String> folderNames, String username) {

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
        );

        // 입력으로 들어온 폴더 이름을 기준으로, 회원이 이미 생성한 폴더들을 조회
        List<Folder> existFolderList = folderRepository.findAllByUserAndNameIn(user, folderNames);

        List<Folder> folderList = new ArrayList<>();
        for (String folderName : folderNames) {
            // 이미 생성한 폴더가 아닌 경우에만 폴더 생성
            if (!isExistFolderName(folderName, existFolderList)) {
                Folder folder = new Folder(folderName, user);
                folderList.add(folder);
            } else {
                throw new IllegalArgumentException("중복된 폴더명 ('" + isExistFolderName(folderName, existFolderList) + "')을 삭제하고 재시도해 주세요.");
            }
        }

        return folderRepository.saveAll(folderList);
    }

    // 로그인한 회원이 등록한 모든 폴더 조회
    public List<Folder> getFolders(User user) {
        return folderRepository.findAllByUser(user);
    }

    // 회원이 등록한 폴더 내 모든 관심상품 조회
    @Transactional(readOnly = true)
    public Page<Product> getProductsInFolder(Long folderId,
                                             int page, int size, String sortBy, boolean isAsc,
                                             User user) {
        // 페이징 처리
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findAllByUserIdAndFolderList_Id(user.getId(), folderId, pageable);
    }

    private boolean isExistFolderName(String folderName, List<Folder> existFolderList) {
        // 기존 폴더 리스트에 folderName이 있는지 확인
        for (Folder existFolder : existFolderList) {
            if (existFolder.getName().equals(folderName)) {
                return true;
            }
        }
        return false;
    }
}
