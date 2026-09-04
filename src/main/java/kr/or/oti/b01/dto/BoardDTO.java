package kr.or.oti.b01.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {

	private Long bno;

	private String title;

	private String content;

	private String writer;

	private LocalDateTime regDate;
	
	private LocalDateTime modDate;
	
	private List<String> fileNames;
	
	private List<RemoveFileDTO> removeFileList; // modify 화면에서 X버튼으로 지운 첨부파일 목록 (S3 삭제용)
}