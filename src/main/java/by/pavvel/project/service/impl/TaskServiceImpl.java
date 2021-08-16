package by.pavvel.project.service.impl;

import by.pavvel.project.entity.Task;
import by.pavvel.project.repository.TaskRepository;
import by.pavvel.project.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<Task> showTasks(int pageNo, int pageSize, String field, String direction){

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(field).ascending() :
                Sort.by(field).descending();

        PageRequest pageRequest = PageRequest.of(pageNo-1,pageSize,sort);
        return taskRepository.findAll(pageRequest);
    }

    public List<Task> getTasks(){
        return taskRepository.findAll();
    }

    public List<Task> getTasksById(List<Long> id){
        return taskRepository.findAllById(id);
    }

    public Task getTask(Long id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Task doesn't exists"));
    }

    @Transactional
    public void addTask(Task task){
        taskRepository.save(task);
    }

    @Transactional
    public void updateTask(Task task) {
        taskRepository.findById(task.getId())
                .orElseThrow(() -> new IllegalStateException(String.format("Task with id %s doesn't exists", task.getId())));
        taskRepository.updateTask(task.getTitle(), task.getHours(), task.getStartDate(), task.getEndDate(),
                task.getStatus(), task.getProject(), task.getId());
    }

    @Transactional
    public void deleteTask(Long id) {
        Optional<Task> taskById = taskRepository.findById(id);
        if (!taskById.isPresent()){
            throw new IllegalStateException(String.format("Task with id %s doesn't exist",id));
        }
        taskRepository.deleteTasksById(id);
    }
}
