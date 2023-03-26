package br.com.erudio.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.erudio.controllers.BookController;
import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.exceptions.RequiredObjectIsNullException;
import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.repositories.BookRepository;

@Service
public class BookServices {
	
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	
	@Autowired
	BookRepository repository;
	
	public List<BookVO> findAll() {
		
		logger.info("Finding all books");
		
		var Books = DozerMapper.parseListObjects( repository.findAll(), BookVO.class);
		Books.stream()
		.forEach(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));
		return Books;
	}

	public BookVO findById(Long id) {
		
		logger.info("Finding one Book!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public BookVO create(BookVO Book) {
		
		if (Book == null) throw new RequiredObjectIsNullException();
		logger.info("Creating one Book!");
		
		var entity = DozerMapper.parseObject(Book, br.com.erudio.model.Book.class);
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	/* public BookVOV2 createV2(BookVOV2 Book) {
		
		logger.info("Creating one Book with V2!");
		
		var entity = mapper.convertVoToEntity(Book);
		var vo = mapper.convertEntityToVo(repository.save(entity));
		
		return vo;
	}
	*/ 
	
	public BookVO update(BookVO Book) {
		
		logger.info("Updating one Book!");
		
		var entity = repository.findById(Book.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));
		
		entity.setAuthor(Book.getAuthor());
		entity.setLaunchDate(Book.getLaunchDate());
		entity.setPrice(Book.getPrice());
		entity.setTitle(Book.getTitle());
		
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	
	public void delete(Long id) {
		
		logger.info("Deleting one Book!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));
		
		repository.delete(entity);
	}
	
}
