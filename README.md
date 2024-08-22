# Desafio DSMovie - Testes de API com RestAssured

Este projeto envolve a implementação de testes de API para o sistema DSMovie, que é um sistema de avaliação de filmes. O desafio é garantir que as funcionalidades do sistema estejam corretas e seguras, especialmente em relação aos endpoints de filmes e de avaliações (`Score`).

## Descrição do Projeto DSMovie

O DSMovie é uma plataforma de filmes onde os usuários podem visualizar filmes e registrar suas avaliações. As operações de inserção, atualização e deleção de filmes são restritas a usuários com papel de **ADMIN**, enquanto as avaliações podem ser feitas por qualquer usuário autenticado (seja **CLIENT** ou **ADMIN**). 

### Entidades Principais

- **Movie**: Representa os filmes no sistema.
- **Score**: Representa a nota de 0 a 5 que cada usuário deu a um filme. A média dessas notas é armazenada na entidade `Movie`.

### Regras de Negócio

1. A visualização dos dados dos filmes é pública.
2. As operações de alteração em filmes (inserção, atualização, deleção) são permitidas apenas para usuários com papel de **ADMIN**.
3. Qualquer usuário logado (**CLIENT** ou **ADMIN**) pode registrar uma avaliação (`Score`).
4. O sistema calcula a média das avaliações de todos os usuários para cada filme e armazena essa média na entidade `Movie`, juntamente com a contagem de votos.

## Testes de API

A seguir estão os testes de API que devem ser implementados utilizando o framework RestAssured.

### MovieControllerRA

1. **findAllShouldReturnOkWhenMovieNoArgumentsGiven**  
   Verifica se a requisição `GET /movies` sem parâmetros retorna o status `200 OK`.

2. **findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty**  
   Verifica se a requisição `GET /movies` com um título de filme como parâmetro retorna uma lista paginada de filmes.

3. **findByIdShouldReturnMovieWhenIdExists**  
   Verifica se a requisição `GET /movies/{id}` retorna um filme quando o `id` existe.

4. **findByIdShouldReturnNotFoundWhenIdDoesNotExist**  
   Verifica se a requisição `GET /movies/{id}` retorna `404 Not Found` quando o `id` não existe.

5. **insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle**  
   Verifica se a requisição `POST /movies` retorna `422 Unprocessable Entity` quando um **ADMIN** tenta inserir um filme com o título em branco.

6. **insertShouldReturnForbiddenWhenClientLogged**  
   Verifica se a requisição `POST /movies` retorna `403 Forbidden` quando um **CLIENT** tenta inserir um filme.

7. **insertShouldReturnUnauthorizedWhenInvalidToken**  
   Verifica se a requisição `POST /movies` retorna `401 Unauthorized` quando o token de autenticação é inválido.

### ScoreControllerRA

1. **saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist**  
   Verifica se a requisição `POST /scores` retorna `404 Not Found` quando o `movieId` não existe.

2. **saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId**  
   Verifica se a requisição `POST /scores` retorna `422 Unprocessable Entity` quando o `movieId` está ausente.

3. **saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero**  
   Verifica se a requisição `POST /scores` retorna `422 Unprocessable Entity` quando a nota (`score`) é menor que zero.


## Conclusão

Esses testes têm como objetivo garantir a integridade das operações da API, assegurando que as regras de negócio sejam cumpridas corretamente e que a segurança da aplicação seja mantida. Ao completar esses testes, você estará contribuindo para a estabilidade e a qualidade do sistema DSMovie.
