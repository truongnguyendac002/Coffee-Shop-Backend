package com.ptit.coffee_shop.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public Map upload(MultipartFile file, String folder) {
        try {
            Map data = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                            "folder", folder,
                            "quality", "auto",
                            "fetch_format", "auto"
                    )
            );
            return data;
        } catch (IOException io) {
            throw new RuntimeException("Image upload fail");
        }
    }

    public Map delete(String imageUrl) {
        try {
            // Trích xuất public_id từ URL
            String publicId = extractPublicId(imageUrl);
            // Xóa ảnh theo public_id
            Map result = this.cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Image delete fail");
        }
    }

    private String extractPublicId(String imageUrl) {
        imageUrl = imageUrl.replace("upload/", "upload/q_auto,f_auto/");
        // Lấy phần public_id từ URL (bỏ phần trước v và bỏ đuôi ảnh như .jpg, .png, .gif, .jpeg)
        String[] urlParts = imageUrl.split("/v[0-9]+/");
        if (urlParts.length > 1) {
            // Loại bỏ đuôi file (ví dụ: .jpg, .png, .gif, .jpeg)
            return urlParts[1].replaceAll("\\.[a-zA-Z]{3,4}$", "");
        }
        throw new IllegalArgumentException("Invalid URL format");
    }


}
