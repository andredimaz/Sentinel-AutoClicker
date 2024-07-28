
# Sentinel-AutoClicker

Este plugin consiste em matar todos os mobs ao alcance do jogador sem precisar clicar em um único botão.
Totalmente configurável.
Desenvolvido para a versão 1.8 da spigot, não testado em outras versões.



## Dependências

Nenhuma


## Placeholders


| Placeholders   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `{status}` | `interna` | Retorna o status do autoclicker. |
| `{cooldown}` | `interna` | Retorna o cooldown atual do jogador. |
| `{range}` | `interna` | Retorna o range atual do jogador. |



## Funcionalidades

- Menu de Gerenciamento.
- Cooldown por permissão.
- Range por permissão.


## FAQ

### O plugin será atualizado?

Sim, será adicionado novas funcionalidades no futuro.

### Que funcionalidades serão adicionadas?

Está nos planos um sistema de melhorias.

### Como posso contactar?

Através do meu discord: **@notdimaz**
Através do meu email: **andredimas213@gmail.com**


## config.yml

```yml
mobs-blacklist:
  - 'COW'
  - 'CHICKEN'

grupos:
  vip:
    ordem: 1
    cooldown: 1
    range: 1
    permissao: ''
  default:
    ordem: 2
    cooldown: 1
    range: 1
    permissao: ''

menu:
  linhas: 3 # Define o número de linhas do menu
  titulo: 'AutoClicker' # Define o título do menu
  alternar:
    slot: 13 # Define o slot onde o item será colocado
    material: '325' # MATERIAL:DATA, ID:DATA, ou TEXTURA para cabeças personalizadas
    nome: '&bAutoClicker' # {status] pode ser usado tanto no nome como na lore
    lore:
      - '&7O autoclicker permite você'
      - '&7matar mobs sem clicar em'
      - '&7um único botão.'
      - ''
      - '&f Delay: &7{cooldown} segundos'
      - '&f Range: &7{range}x{range}'
      - ''
      - '&fClique para {status}&f.'
  #item1:  É possivel criar itens de decoração
  #  slot: 14 
  #  material: 'SIGN'
  #  nome: '&6Informação'
  #  lore:
  #    - '&fDelay: &7{cooldown}'
  #    - ''
  #    - ''
      
mensagens: #deixe vazio para não usar
  ativado: '&aAutoClick ativado com sucesso'
  desativado: '&cAutoClick desativado com sucesso' 
  ja-ativado: '&aO Autoclick já se encontra ativado.'
  ja-desativado: '&cO Autoclick já se encontra desativado.'
  comando-invalido: '&cUso inválido. Use /ac [on|off]'
  
  
status:
  ativar: '&aativar'
  desativar: '&cdesativar'
```


## Exemplos de como pode ser usado.

 ![Menu](https://imgur.com/t8wOg5R.png)
