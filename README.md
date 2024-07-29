
# Sentinel-AutoClicker

Este plugin consiste em matar todos os mobs ao alcance do jogador sem precisar clicar em um único botão.
Totalmente configurável.
Desenvolvido para a versão 1.8 da spigot, não testado em outras versões.



## Dependências

Vault

## Comandos

| Comandos   | Descrição                           |
| :---------- | :---------------------------------- |
| `/ac` | Abre o menu principal. |
| `/ac on` | Ativa o autoclicker. |
| `/ac off` | Desativa o autoclicker. |
| `/ac reload` | Recarrega a configuração. |
| `/ac reset <jogador> ou *` | Reseta as melhorias de um jogador. "*" reseta de todos os jogadores. |
| `/ac ajuda` | Mostra todos os comandos. |

## Permissões

| Permissoes   | Descrição                           |
| :---------- | :---------------------------------- |
| `autoclicker.admin` | Permissão para /ac reset e /ac reload. |

## Placeholders


| Placeholders   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `{status}` | `interna` | Retorna o status do autoclicker. |
| `{cooldown}` | `interna` | Retorna o cooldown atual do jogador. |
| `{range}` | `interna` | Retorna o range atual do jogador. |
| `{cooldown_next}` | `interna` | Retorna o cooldown do próximo nivel. |
| `{range_next}` | `interna` | Retorna o range do próximo nivel. |
| `{cooldown_nivel}` | `interna` | Retorna a lore da secção lores do cooldown. |
| `{range_nivel}` | `interna` | Retorna a lore da secção lores do range. |
| `{status-lore-cooldown}` | `interna` | Retorna a lore na secção lores de status-lore. |
| `{status-lore-range}` | `interna` | Retorna a lore na secção lores de status-lore. |
| `{money}` | `interna` | Retorna o money necessário para evoluir. |
| `{xp}` | `interna` | Retorna o xp necessário para evoluir. |



## Funcionalidades

- Menu de Gerenciamento.
- Cooldown por permissão.
- Range por permissão.
- Melhorias de Cooldown e Range.
- Menus totalmente configuráveis.


## FAQ

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
    permissao: ''
    melhorias:
      cooldown:
        1:
          custos:
            custo1:
              preco: 10
              tipo: money
          cooldown: 1.5
        2:
          custos:
            custo1:
              preco: 20
              tipo: money
            custo2:
              preco: 5
              tipo: xp
          cooldown: 1.2
      range:
        1:
          custos:
            custo1:
              preco: 10
              tipo: money
          range: 2
        2:
          custos:
            custo1:
              preco: 20
              tipo: money
            custo2:
              preco: 50
              tipo: money
          range: 4
  default:
    ordem: 2
    permissao: ''
    melhorias:
      cooldown:
        1:
          custos:
            custo1:
              preco: 10
              tipo: money
          cooldown: 2
      range:
        1:
          custos:
            custo1:
              preco: 10
              tipo: money
          range: 1

menus:
  principal:
    linhas: 3
    titulo: 'AutoClicker'
    alternar:
      slot: 12
      material: '325'
      nome: '&bAutoClicker'
      lore:
        - '&7O autoclicker permite você'
        - '&7matar mobs sem clicar em'
        - '&7um único botão.'
        - ''
        - '&eyClique para {status}&f.'
    melhorar:
      slot: 14
      material: '384'
      nome: '&bMelhorias'
      lore:
        - '&7Melhore as propriedades'
        - '&7do seu autoclicker.'
        - ''
        - '&f Delay: &7{cooldown} segundos'
        - '&f Range: &7{range}x{range}'
        - ''
        - '&aClique para acessar.'

  melhorias:
    linhas: 3
    titulo: 'AutoClicker - Melhorias'
    cooldown:
      slot: 11
      material: '347'
      nome: '&bCooldown'
      lore:
        - '&7Melhore o tempo do'
        - '&7autoclicker'
        - ''
        - '&f Delay: &7{cooldown_nivel}'
        - '{custos}'
        - ''
        - '&f{status-lore-cooldown}'
    range:
      slot: 15
      material: '333'
      nome: '&bRange'
      lore:
        - '&7Melhore o range do'
        - '&7autoclicker'
        - ''
        - '&f Range: &7{range_nivel}'
        - '{custos}'
        - ''
        - '&f{status-lore-range}'
    voltar:
      slot: 13
      material: '262'
      nome: '&cVoltar'
      lore:
        - '&7Clique para voltar'
        - '&7ao menu principal'

mensagens:
  ativado: '&aAutoClick ativado com sucesso'
  desativado: '&cAutoClick desativado com sucesso'
  ja-ativado: '&aO Autoclick já se encontra ativado.'
  ja-desativado: '&cO Autoclick já se encontra desativado.'
  comando-invalido: '&cUso inválido. Use /ac [on|off]'

status:
  ativar: '&aativar'
  desativar: '&cdesativar'

lores:
  nivel:
    cooldown:
      melhorar: '&7{cooldown}s &f-> &7{cooldown_next}s'
      maximo: '&7{cooldown}s'
    range:
      melhorar: '&7{range} &f-> &7{range_next}'
      maximo: '&7{range}x{range}'
  status-lore:
    pode-evoluir: '&aClique para melhorar.'
    sem-saldo: '&cSaldo insuficiente'
    nivel-maximo: '&cNivel máximo atingido'

custos-lore:
  nome: '&f Custo:'
  money: '&a  {money} coins'
  xp: '&e  {xp} Niveis'

```


## Exemplos de como pode ser usado.

 ![Menu](https://imgur.com/xyVXBdB.png)  ![Menu](https://imgur.com/qASzkbu.png)  ![Menu](https://imgur.com/c8COxQg.png)
