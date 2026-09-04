package kr.or.oti.b01.dto.upload;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import kr.or.oti.b01.util.S3Uploader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class UploadResultDTO {
	private String uuid;
	private String filename;
	private String realFilename; // 삭제/URL 생성 시 쓸 "uuid_원본파일명" 키 - S3 URL로 덮어쓰지 않음
	private boolean img;
	private String s3Url; // register 화면 미리보기 전용 (실제 S3 URL)
	
    public String getLink() {
        return img ? "s_" + realFilename : realFilename; 
    }
    
    public UploadResultDTO(String uploadPath, MultipartFile file, S3Uploader s3Uploader) {
        this.uuid = UUID.randomUUID().toString();
        this.filename = file.getOriginalFilename();
        this.realFilename = this.uuid + "_" + this.filename; 
        this.img = false;
        
        Path path = Paths.get(uploadPath, realFilename);
        
        try {
            file.transferTo(path);
            
            if (file.getContentType().startsWith("image/")) {
                
                Path thumbFile = Paths.get(uploadPath, "s_" + realFilename);
                
                //섬네일 이미지 저장 
                Thumbnailator.createThumbnail(path.toFile(), thumbFile.toFile(), 200, 200);
                
                this.img = true;
            }
            
          //S3로 파일을 업로드 한다. (realFilename은 "uuid_원본명" 키 그대로 유지 - split("_") 깨짐 방지)
            this.s3Url = s3Uploader.upload(uploadPath + File.separator + this.realFilename);

            log.info("S3에 업로드된 URL = " + this.s3Url);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}